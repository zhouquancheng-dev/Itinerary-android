package com.example.network.service

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST

interface CaptchaService {

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
    @POST("/validate/gt-captcha")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    suspend fun verifyCaptcha(
        @Field("captcha_id") captchaId: String,
        @Field("lot_number") lotNumber: String,
        @Field("captcha_output") captchaOutput: String,
        @Field("pass_token") passToken: String,
        @Field("gen_time") genTime: String,
        @Field("expected_action") action: String
    ): Boolean

}