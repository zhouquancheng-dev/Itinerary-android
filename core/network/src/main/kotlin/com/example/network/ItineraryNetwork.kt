package com.example.network

import com.example.model.captcha.AliCaptchaRequest
import com.example.model.sms.TokenVerifyRequest
import com.example.network.service.UserService
import com.example.network.service.WeatherService
import com.example.network.url.Q_WEATHER_BASE_URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ItineraryNetwork @Inject constructor(serviceCreator: ServiceCreator) {
    private val userService = serviceCreator.createRequestApi(UserService::class.java)
    private val weatherService = serviceCreator.createRequestApi(WeatherService::class.java, Q_WEATHER_BASE_URL)

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
    suspend fun verifyCaptcha(request: AliCaptchaRequest) = userService.verifyCaptcha(request)

    /**
     * TIM登录票据
     */
    suspend fun getUserSig(userId: String) = userService.getUserSig(userId)

    /**
     * 获取OSS STS临时凭证
     */
    suspend fun getStsToken() = userService.getStsToken()

    /**
     * 实时天气
     */
    suspend fun realtimeWeather(location: String) = weatherService.realtimeWeather(location)
}