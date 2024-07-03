package com.example.login.vm

import android.content.Context
import android.view.Gravity
import androidx.lifecycle.viewModelScope
import cn.jiguang.verifysdk.api.JVerificationInterface
import com.blankj.utilcode.util.LogUtils
import com.example.common.data.Constants.TIM_TAG
import com.example.common.data.DatastoreKey.IS_LOGIN_STATUS
import com.example.common.data.DatastoreKey.TIM_USER_ID
import com.example.common.util.DataStoreUtils.putBooleanSync
import com.example.common.util.DataStoreUtils.putString
import com.example.common.util.DataStoreUtils.putStringSync
import com.example.common.util.RSADecrypt.decrypt
import com.example.common.vm.BaseViewModel
import com.example.login.R
import com.example.login.client.JiGuangClient
import com.example.login.client.JiGuangClient.Companion.AUTH_CODE_SUCCESS
import com.example.login.state.DialogType
import com.example.model.SUCCESS
import com.example.model.SUCCESS_STRING
import com.example.model.captcha.AliCaptchaRequest
import com.example.model.captcha.SuccessResponse
import com.example.model.sms.TokenVerifyRequest
import com.example.network.ItineraryNetwork
import com.example.network.captcha.AliYunCaptchaClient
import com.example.network.captcha.CAPTCHA_ID
import com.example.network.listener.CaptchaListener
import com.hjq.toast.ToastParams
import com.hjq.toast.Toaster
import com.hjq.toast.style.CustomToastStyle
import com.tencent.imsdk.BaseConstants.ERR_SVR_ACCOUNT_USERSIG_EXPIRED
import com.tencent.imsdk.BaseConstants.ERR_USER_SIG_EXPIRED
import com.tencent.imsdk.v2.V2TIMCallback
import com.tencent.imsdk.v2.V2TIMManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val network: ItineraryNetwork,
    private val jiGuangClient: JiGuangClient,
    private val loginRepository: LoginRepository
) : BaseViewModel() {

    private val _loginAuthState = MutableStateFlow(DialogType.NONE)
    val loginAuthState = _loginAuthState.asStateFlow()

    private val _sendingVerifyCode = MutableStateFlow(false)
    val sendingVerifyCode = _sendingVerifyCode.asStateFlow()

    private val _verifying = MutableStateFlow(false)
    val verifying = _verifying.asStateFlow()

    // 发送验证码
    fun sendSmsCode(phoneNumber: String, onSuccess: () -> Unit) {
        fetchData { network.sendSmsCode(phoneNumber) }
            .onStart { _sendingVerifyCode.value = true }
            .onEach { response ->
                _sendingVerifyCode.value = false
                if (response.status == SUCCESS) {
                    onSuccess()
                }
            }
            .launchIn(viewModelScope)
    }

    // 验证码核验
    @OptIn(ExperimentalCoroutinesApi::class)
    fun verifySmsCode(context: Context, phoneNumber: String, verifyCode: String, onFailure: () -> Unit, onSuccess: () -> Unit) {
        fetchData { network.verifySmsCode(phoneNumber, verifyCode) }
            .onStart { _verifying.value = true }
            .flatMapMerge { response ->
                if (response.status == SUCCESS) {
                    val cachedUserSig = loginRepository.getCachedUserSig()
                    if (cachedUserSig.isNotEmpty()) {
                        flowOf(cachedUserSig)
                    } else {
                        fetchData { network.getUserSig(phoneNumber) }
                            .map { userResp ->
                                if (userResp.status == SUCCESS) {
                                    userResp.data
                                } else {
                                    null
                                }
                            }
                    }
                } else {
                    flow {
                        _verifying.value = false
                        onFailure()
                        showNotify(context.getString(R.string.sms_error), false)
                    }
                }
            }
            .filterNotNull()
            .onEach { userSig ->
                // 登录IM
                loginTIM(
                    context, phoneNumber, userSig,
                    onSuccess = {
                        _verifying.value = false
                        putBooleanSync(IS_LOGIN_STATUS, true)
                        showNotify(context.getString(R.string.login_success), true)
                        onSuccess()
                    },
                    onError = {
                        _verifying.value = false
                        showNotify(context.getString(R.string.login_failure), false)
                        onFailure()
                    }
                )
            }
            .catch {
                _verifying.value = false
                showNotify(context.getString(R.string.login_exception), false)
            }
            .launchIn(viewModelScope)
    }

    // 极光预取号
    fun preLogin(context: Context) = jiGuangClient.preLogin(context)

    // 拉起一键登录授权页
    @OptIn(ExperimentalCoroutinesApi::class)
    fun loginAuth(context: Context, onSuccess: () -> Unit) {
        if (!JVerificationInterface.checkVerifyEnable(context)) {
            Toaster.show("当前网络环境不支持认证，请开启数据网络")
            return
        }

        _loginAuthState.value = DialogType.PULL_AUTH
        jiGuangClient.loginAuth(context, object : JiGuangClient.AuthListener {
            override fun onAuthEvent(event: Int, msg: String?) {
                if (event == 2) {
                    _loginAuthState.value = DialogType.NONE
                }
            }

            override fun onLoginAuthResult(code: Int, content: String?, operator: String?, operatorReturn: Any?) {
                if (code == AUTH_CODE_SUCCESS) {
                    val request = TokenVerifyRequest(content!!, null)
                    fetchData { network.loginTokenVerify(request) }
                        .onStart { _loginAuthState.value = DialogType.LOGIN }
                        .flatMapMerge { response ->
                            if (response.status == SUCCESS) {
                                // 解密拿到登录成功的手机号
                                val cryptograph = response.data?.phone ?: ""
                                val fileContent = loginRepository.readAssetFile(context, "private_key.txt")
                                val decryptPhoneNumber = decrypt(cryptograph, fileContent)
                                LogUtils.d("一键登录成功解密后的手机号: $decryptPhoneNumber")

                                val cachedUserSig = loginRepository.getCachedUserSig()
                                if (cachedUserSig.isNotEmpty()) {
                                    flowOf(Pair(decryptPhoneNumber, cachedUserSig))
                                } else {
                                    fetchData { network.getUserSig(decryptPhoneNumber) }
                                        .map { userSigResp ->
                                            if (userSigResp.status == SUCCESS) {
                                                Pair(decryptPhoneNumber, userSigResp.data)
                                            } else {
                                                null
                                            }
                                        }
                                }
                            } else {
                                flow {
                                    LogUtils.e("登录验证失败; code: ${response.status} content: ${response.msg}")
                                    _loginAuthState.value = DialogType.NONE
                                    showNotify(context.getString(R.string.login_failure), false)
                                }
                            }
                        }
                        .filterNotNull()
                        .onEach { pairData ->
                            // 登录IM
                            loginTIM(
                                context, pairData.first, pairData.second ?: "",
                                onSuccess = {
                                    _loginAuthState.value = DialogType.NONE
                                    putBooleanSync(IS_LOGIN_STATUS, true)
                                    showNotify(context.getString(R.string.login_success), true)

                                    onSuccess()
                                },
                                onError = {
                                    _loginAuthState.value = DialogType.NONE
                                    showNotify(context.getString(R.string.login_failure), true)
                                }
                            )
                        }
                        .catch {
                            _loginAuthState.value = DialogType.NONE
                            showNotify(context.getString(R.string.login_exception), false)
                        }
                        .launchIn(viewModelScope)
                } else {
                    _loginAuthState.value = DialogType.NONE
                    showNotify(context.getString(R.string.login_auth_failure), false)
                }
            }
        })
    }

    // 登录TIM
    private fun loginTIM(context: Context, userId: String, userSig: String, onSuccess: () -> Unit, onError: () -> Unit) {
        V2TIMManager.getInstance().login(userId, userSig, object : V2TIMCallback {
            override fun onSuccess() {
                LogUtils.iTag(TIM_TAG, "IM登录成功")

                val loginUserId = V2TIMManager.getInstance().loginUser
                loginRepository.putIMUserId(loginUserId)
                loginRepository.cacheUserSig(userSig)

                onSuccess()
            }

            override fun onError(code: Int, desc: String?) {
                // 如果返回以下错误码，表示使用 UserSig 已过期，请您使用新签发的 UserSig 进行再次登录。
                // 1. ERR_USER_SIG_EXPIRED（6206）
                // 2. ERR_SVR_ACCOUNT_USERSIG_EXPIRED（70001）
                // 注意：其他的错误码，请不要在这里调用登录接口，避免 IM SDK 登录进入死循环。
                LogUtils.iTag(TIM_TAG, "IM登录失败 code: $code, desc: $desc")
                if (code == ERR_USER_SIG_EXPIRED || code == ERR_SVR_ACCOUNT_USERSIG_EXPIRED) {
                    // 重新生成 userSig 并重新登录
                    fetchData { network.getUserSig(userId) }
                        .onEach { userResp ->
                            if (userResp.status == SUCCESS) {
                                userResp.data?.let { newUserSig ->
                                    loginTIM(context, userId, newUserSig, onError, onSuccess)
                                }
                            }
                        }
                        .catch {
                            onError()
                            showNotify(context.getString(R.string.login_exception), false)
                        }
                        .launchIn(viewModelScope)
                } else {
                    onError()
                    showNotify(context.getString(R.string.login_exception), false)
                }
            }
        })
    }

    // 阿里云行为验证码二次核验
    fun launchWithCaptcha(context: Context, onSuccess: () -> Unit) {
        AliYunCaptchaClient.launchWithCaptcha().setCaptchaListener(object : CaptchaListener {
            override fun onSuccess(response: SuccessResponse) {
                val request = AliCaptchaRequest(
                    response.lotNumber,
                    response.captchaOutput,
                    response.passToken,
                    response.genTime,
                    CAPTCHA_ID
                )

                fetchData { network.verifyCaptcha(request) }
                    .onEach { vResponse ->
                        if (vResponse.result == SUCCESS_STRING) {
                            onSuccess()
                        } else {
                            LogUtils.e("二次验证失败; result: ${vResponse.result}, reason: ${vResponse.reason}")
                            showNotify(context.getString(R.string.captcha_failure), false)
                        }
                    }
                    .launchIn(viewModelScope)
            }
        })
    }

    fun showNotify(message: String, isSuccess: Boolean) {
        val params = ToastParams().apply {
            text = message
            style = if (isSuccess) {
                CustomToastStyle(com.example.ui.R.layout.toast_custom_view_success, Gravity.CENTER)
            } else {
                CustomToastStyle(com.example.ui.R.layout.toast_custom_view_error, Gravity.CENTER)
            }
        }
        Toaster.show(params)
    }

}