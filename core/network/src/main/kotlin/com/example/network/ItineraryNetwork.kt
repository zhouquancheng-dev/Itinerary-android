package com.example.network

import com.example.model.captcha.AliCaptchaRequest
import com.example.model.sms.TokenVerifyRequest
import com.example.network.service.CaptchaService
import com.example.network.service.IMService
import com.example.network.service.UserService
import okhttp3.MultipartBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ItineraryNetwork @Inject constructor(serviceCreator: ServiceCreator) {
    private val userService = serviceCreator.createRequestApi(UserService::class.java)
    private val captchaService = serviceCreator.createRequestApi(CaptchaService::class.java)
    private val imService = serviceCreator.createRequestApi(IMService::class.java)

    /**
     * 极光一键登录验证
     */
    suspend fun loginTokenVerify(request: TokenVerifyRequest) =
        userService.loginTokenVerify(request)

    /**
     * 发送验证码
     */
    suspend fun sendSmsCode(phoneNumber: String) = userService.sendSmsCode(phoneNumber)

    /**
     * 验证码核验
     */
    suspend fun verifySmsCode(phoneNumber: String, verifyCode: String) =
        userService.verifySmsCode(phoneNumber, verifyCode)

    /**
     * 阿里云行为验证码二次核验
     */
    suspend fun verifyCaptcha(request: AliCaptchaRequest) = captchaService.verifyCaptcha(request)

    /**
     * TIM登录票据
     */
    suspend fun getUserSig(userId: String) = imService.getUserSig(userId)

    /**
     * oss 上传文件
     */
    suspend fun uploadFile(file: MultipartBody.Part) = imService.uploadFile(file)
}