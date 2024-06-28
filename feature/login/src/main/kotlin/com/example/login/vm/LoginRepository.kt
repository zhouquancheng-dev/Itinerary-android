package com.example.login.vm

import com.blankj.utilcode.util.LogUtils
import com.example.model.captcha.AliCaptchaRequest
import com.example.model.sms.TokenVerifyRequest
import com.example.network.ItineraryNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val network: ItineraryNetwork
) {

    /**
     * 发送验证码
     */
    fun sendSmsCode(phoneNumber: String) = flow {
        val response = network.sendSmsCode(phoneNumber)
        emit(Result.success(response))
    }.catch { e ->
        LogUtils.e("发送验证码异常; Exception: $e")
        emit(Result.failure(e))
    }.flowOn(Dispatchers.IO)

    /**
     * 验证码校验
     */
    fun verifySmsCode(phoneNumber: String, verifyCode: String) = flow {
        val response = network.verifySmsCode(phoneNumber, verifyCode)
        emit(Result.success(response))
    }.catch { e ->
        LogUtils.e("验证码校验异常; Exception: $e")
        emit(Result.failure(e))
    }.flowOn(Dispatchers.IO)

    /**
     * 极光一键登录验证
     */
    fun loginTokenVerify(request: TokenVerifyRequest) = flow {
        val response = network.loginTokenVerify(request)
        emit(Result.success(response))
    }.catch { e ->
        LogUtils.e("一键登录验证异常; Exception: $e")
        emit(Result.failure(e))
    }.flowOn(Dispatchers.IO)


    /**
     * 阿里云行为验证码二次核验
     */
    fun verifyCaptcha(request: AliCaptchaRequest) = flow {
        val response = network.verifyCaptcha(request)
        emit(Result.success(response))
    }.catch { e ->
        LogUtils.e("行为验证码核验异常; Exception: $e")
        emit(Result.failure(e))
    }.flowOn(Dispatchers.IO)

}
