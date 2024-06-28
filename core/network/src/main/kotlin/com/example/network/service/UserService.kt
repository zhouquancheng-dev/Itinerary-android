package com.example.network.service

import com.example.model.sms.TokenVerifyRequest
import com.example.model.sms.TokenVerifyResponse
import com.example.model.BaseResponse
import com.example.model.sms.CheckSmsResponse
import com.example.model.sms.SendSmsResponse
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface UserService {

    /**
     * 极光一键登录验证
     *
     * @param request
     * {
     *    "loginToken": "",
     *    "exID": null
     * }
     */
    @POST("/login/tokenVerify")
    suspend fun loginTokenVerify(
        @Body request: TokenVerifyRequest
    ): BaseResponse<TokenVerifyResponse>

    /**
     * 发送验证码
     *
     * @param phoneNumber 手机号
     * @param codeLength 验证码长度，支持4-8位，默认值：6位
     * @param validTime 验证码有效时长，默认值：300s
     * @param sendInterval 发送时间间隔，默认值：60s
     */
    @POST("/login/sendSmsCode")
    @FormUrlEncoded
    suspend fun sendSmsCode(
        @Field("phoneNumber") phoneNumber: String,
        @Field("codeLength") codeLength: Long = 6L,
        @Field("validTime") validTime: Long = 300L,
        @Field("sendInterval") sendInterval: Long = 60L,
    ): BaseResponse<SendSmsResponse>

    /**
     * 验证码核验
     *
     * @param phoneNumber 手机号
     * @param verifyCode 验证码
     */
    @POST("/login/verifyCode")
    @FormUrlEncoded
    suspend fun verifySmsCode(
        @Field("phoneNumber") phoneNumber: String,
        @Field("verifyCode") verifyCode: String
    ): BaseResponse<CheckSmsResponse>

}