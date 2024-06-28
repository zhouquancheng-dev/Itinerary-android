package com.example.login.vm

import android.content.Context
import android.view.Gravity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.jiguang.verifysdk.api.JVerificationInterface
import com.blankj.utilcode.util.LogUtils
import com.example.login.R
import com.example.login.client.JiGuangClient
import com.example.login.state.DialogType
import com.example.model.captcha.AliCaptchaRequest
import com.example.model.captcha.SuccessResponse
import com.example.model.sms.TokenVerifyRequest
import com.example.network.captcha.AliYunCaptchaClient
import com.example.network.captcha.CAPTCHA_ID
import com.example.network.listener.CaptchaListener
import com.hjq.toast.ToastParams
import com.hjq.toast.Toaster
import com.hjq.toast.style.CustomToastStyle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val jiGuangClient: JiGuangClient,
    private val loginRepository: LoginRepository
) : ViewModel() {

    private val _dialogState = MutableStateFlow(DialogType.NONE)
    val dialogState = _dialogState.asStateFlow()

    private val _gettingCode = MutableStateFlow(false)
    val gettingCode = _gettingCode.asStateFlow()

    private val _verifying = MutableStateFlow(false)
    val verifying = _verifying.asStateFlow()

    // 发送验证码
    fun sendSmsCode(phoneNumber: String, onSuccess: () -> Unit) {
        loginRepository.sendSmsCode(phoneNumber)
            .onStart { _gettingCode.value = true }
            .onEach { result ->
                result.onSuccess { response ->
                    if (response.status == 200) {
                        _gettingCode.value = false
                        onSuccess()
                    }
                }.onFailure {
                    _gettingCode.value = false
                }
            }
            .launchIn(viewModelScope)
    }

    // 验证码核验
    fun verifySmsCode(
        context: Context,
        phoneNumber: String,
        verifyCode: String,
        onFailure: () -> Unit,
        onSuccess: () -> Unit
    ) {
        loginRepository.verifySmsCode(phoneNumber, verifyCode)
            .onStart {  _verifying.value = true }
            .onEach { result ->
                result.onSuccess { response ->
                    _verifying.value = false
                    if (response.status == 200) {
                        showToast(context, true)
                        onSuccess()
                    } else {
                        val params = ToastParams()
                        params.text = context.getString(R.string.login_error3)
                        params.style = CustomToastStyle(R.layout.toast_custom_view_error, Gravity.CENTER)
                        Toaster.show(params)
                        onFailure()
                    }
                }.onFailure {
                    _verifying.value = false
                    showToast(context, false)
                    onFailure()
                }
            }
            .launchIn(viewModelScope)
    }

    // 极光预取号
    fun preLogin(context: Context) = jiGuangClient.preLogin(context)

    // 极光拉起一键登录授权页
    fun loginAuth(context: Context, onSuccess: () -> Unit) {
        if (!JVerificationInterface.checkVerifyEnable(context)) {
            Toaster.show("当前网络环境不支持认证，请开启数据网络")
            return
        }

        viewModelScope.launch {
            _dialogState.value = DialogType.PULL_AUTH
            delay(300)

            jiGuangClient.loginAuth(context, object : JiGuangClient.AuthListener {
                override fun onAuthEvent(event: Int, msg: String?) {
                    if (event == 2) {
                        _dialogState.value = DialogType.NONE
                    }
                }

                override fun onLoginAuthResult(
                    code: Int, content: String?, operator: String?, operatorReturn: Any?
                ) {
                    if (code == 6000) {
                        val request = TokenVerifyRequest(content!!, null)
                        loginRepository.loginTokenVerify(request)
                            .onStart { _dialogState.value = DialogType.LOGIN }
                            .onEach { result ->
                                result.onSuccess { response ->
                                    _dialogState.value = DialogType.NONE
                                    if (response.status == 200) {
                                        showToast(context, true)
                                        LogUtils.d("登录验证成功; code: ${response.data}")
                                        onSuccess()
                                    } else {
                                        showToast(context, false)
                                        LogUtils.e("登录验证失败; code: ${response.status} content: ${response.msg}")
                                    }
                                }.onFailure {
                                    _dialogState.value = DialogType.NONE
                                    showToast(context, false)
                                }
                            }
                            .launchIn(viewModelScope)
                    } else {
                        _dialogState.value = DialogType.NONE
                        val params = ToastParams()
                        params.text = context.getString(R.string.login_auth_failure)
                        params.style = CustomToastStyle(R.layout.toast_custom_view_error, Gravity.CENTER)
                        Toaster.show(params)
                    }
                }
            })
        }
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

                // 请求服务端二次验证
                loginRepository.verifyCaptcha(request)
                    .onEach { result ->
                        result.onSuccess { response ->
                            Toaster.cancel()
                            if (response.result == "success") {
                                LogUtils.d("二次验证成功; response: $response")
                                onSuccess()
                            } else {
                                LogUtils.d("二次验证失败; result: ${response.result}, reason: ${response.reason}")
                                val params = ToastParams()
                                params.text = context.getString(R.string.captcha_failure)
                                params.style = CustomToastStyle(R.layout.toast_custom_view_error, Gravity.CENTER)
                                Toaster.show(params)
                            }
                        }
                    }
                    .launchIn(viewModelScope)
            }
        })
    }

    private fun showToast(context: Context, isSuccess: Boolean) {
        val params = ToastParams().apply {
            text = if (isSuccess) {
                context.getString(R.string.login_success)
            } else {
                context.getString(R.string.login_failure)
            }
            style = if (isSuccess) {
                CustomToastStyle(R.layout.toast_custom_view_success, Gravity.CENTER)
            } else {
                CustomToastStyle(R.layout.toast_custom_view_error, Gravity.CENTER)
            }
        }
        Toaster.show(params)
    }
}