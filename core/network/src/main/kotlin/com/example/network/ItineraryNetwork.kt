package com.example.network

import com.example.network.service.UserService
import com.example.network.service.SmsService
import com.example.network.service.CaptchaService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ItineraryNetwork @Inject constructor() {
    private val userService = ServiceCreator.createRequestApi<UserService>()
    private val smsService = ServiceCreator.createRequestApi<SmsService>()
    private val captchaService = ServiceCreator.createRequestApi<CaptchaService>()

    /**
     * 用户登录
     * @param token 令牌
     * @param username 用户名
     * @param password 密码
     */
    suspend fun login(token: String, username: String, password: String) =
        userService.login(token, username, password)

    /**
     * 用户注册
     * @param username 用户名
     * @param password 密码
     */
    suspend fun register(username: String, password: String) =
        userService.register(username, password)

    /**
     * 用户登出
     * @param token 令牌
     */
    suspend fun logout(token: String) = userService.logout(token)

    /**
     * 自动登录
     * @param token 令牌
     */
    suspend fun autoLogin(token: String) = userService.autoLogin(token)

    /**
     * 存储验证码信息
     * @param phoneNumber 手机号码
     * @param smsCode 验证码
     * @param bizId 发送回执id
     * @param sendTime 发送时间
     */
    suspend fun saveSmsData(phoneNumber: String, smsCode: String, bizId: String, sendTime: String) =
        smsService.saveSmsData(phoneNumber, smsCode, bizId, sendTime)

    /**
     * 校验短信验证码
     * @param phoneNumber 手机号码
     * @param smsCode 验证码
     * @param bizId 发送回执id
     */
    suspend fun checkSms(phoneNumber: String, smsCode: String, bizId: String) =
        smsService.checkSms(phoneNumber, smsCode, bizId)

    /**
     * 极验验证码二次校验
     * @param captchaId 验证 id
     * @param lotNumber 验证流水号
     * @param captchaOutput 验证输出信息
     * @param passToken 验证通过标识
     * @param genTime 验证通过时间戳
     * @param action 客户端验证行为，如: login
     * @return Boolean 校验成功 true 否则 false
     */
    suspend fun verifyCaptcha(
        captchaId: String,
        lotNumber: String,
        captchaOutput: String,
        passToken: String,
        genTime: String,
        action: String
    ) = captchaService.verifyCaptcha(
        captchaId,
        lotNumber,
        captchaOutput,
        passToken,
        genTime,
        action
    )
}