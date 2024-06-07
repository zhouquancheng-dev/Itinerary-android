package com.example.login.vm

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.util.DataStoreUtils
import com.example.login.obj.LoginState
import com.example.model.CaptchaResponse
import com.example.network.captcha.GTCaptcha4Client
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.system.measureTimeMillis

@HiltViewModel
class UserViewModel @Inject constructor(
    private val context: Application,
    private val userRepository: UserRepository
) : AndroidViewModel(context) {

    private val _openAlertDialog = MutableStateFlow(false)
    val openAlertDialog = _openAlertDialog.asStateFlow()

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     */
    fun login(username: String, password: String, callback: () -> Unit) {
        _openAlertDialog.value = true
        userRepository.login(username, password)
            .onEach { userResponse ->
                delay(1500)
                val message = when {
                    userResponse.code == 200 -> {
                        "登录成功"
                    }
                    userResponse.msg == "EXPIRED_TOKEN_RESPONSE" -> {
                        "登录已过期"
                    }
                    else -> {
                        "用户名或密码错误"
                    }
                }
                _openAlertDialog.value = false
                showToast(message)

                if (userResponse.code == 200) {
                    LoginState.login = true
                    userResponse.data?.let { token ->
                        DataStoreUtils.putString("", token)
                    }
                    withContext(Dispatchers.Main) {
                        callback()
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    /**
     * 用户注册
     * @param username 用户名
     * @param password 密码
     */
    fun register(username: String, password: String, callback: () -> Unit) {
        userRepository.register(username, password)
            .onEach { userResponse ->
                val message = if (userResponse.code == 200) {
                    "注册成功"
                } else {
                    "注册失败"
                }
                showToast(message)

                if (userResponse.code == 200) {
                    withContext(Dispatchers.Main) {
                        callback()
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    /**
     * 用户退出登录
     * @param callback 成功回调
     */
    fun logout(callback: () -> Unit) {
        userRepository.logout()
            .onEach { userResponse ->
                val message = if (userResponse.code == 200) {
                    "退出成功"
                } else {
                    "退出失败"
                }
                showToast(message)

                if (userResponse.code == 200) {
                    // 设置为未登录状态并删除token
                    LoginState.logout()
                    withContext(Dispatchers.Main) {
                        callback()
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    /**
     * 自动登录
     */
    fun autoLogin(successCallback: () -> Unit, failureCallback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val executionTime = measureTimeMillis {
                userRepository.autoLogin()
                    .onEach { userResponse ->
                        if (userResponse.code == 200) {
                            withContext(Dispatchers.Main) {
                                successCallback()
                            }
                        } else {
                            showToast(userResponse.msg)
                            withContext(Dispatchers.Main) {
                                failureCallback()
                            }
                        }
                    }
                    .launchIn(viewModelScope)
            }
            delay(executionTime)
        }
    }

    /**
     * 发送验证码
     * @param phoneNumber 手机号
     * @param callback 成功回调
     */
    fun sendSmsCode(phoneNumber: String, callback: (String) -> Unit) {

    }

    /**
     * 校验短信验证码
     * @param phoneNumber 手机号码
     * @param smsCode 验证码
     * @param bizId 发送回执id
     * @param callback 成功回调
     */
    fun verifySmsCode(
        phoneNumber: String,
        smsCode: String,
        bizId: String,
        callback: () -> Unit,
    ) {
        userRepository.verifySmsCode(phoneNumber, smsCode, bizId)
            .onEach { userResponse ->
                val message = if (userResponse.code == 200) {
                    "验证成功"
                } else {
                    userResponse.msg
                }
                showToast(message)

                if (userResponse.code == 200) {
                    LoginState.login = true
                    withContext(Dispatchers.Main) {
                        callback()
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    /**
     * 极验验证码二次校验
     */
    fun safetyNetCaptcha(username: String, password: String, callback: () -> Unit) {
        GTCaptcha4Client
            .launchWithCaptcha()
            .setCaptchaListener(object : GTCaptcha4Client.CaptchaListener {
                override fun onSuccess(captchaResponse: CaptchaResponse) {
                    userRepository.verifyCaptcha(
                        captchaResponse.captchaId,
                        captchaResponse.lotNumber,
                        captchaResponse.captchaOutput,
                        captchaResponse.passToken,
                        captchaResponse.genTime,
                        "login"
                    ).onEach { valid ->
                        if (valid) {
                            login(username, password, callback)
                        } else {
                            showToast("验证失败")
                        }
                    }.launchIn(viewModelScope)
                }
            })
    }

    /**
     * Toast
     * @param message 信息
     */
    private suspend fun showToast(message: String) {
        withContext(Dispatchers.Main) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

}