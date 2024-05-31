package com.example.network.service

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST
import com.example.model.UserResponse
import com.example.model.SmsResponse

interface SmsService {

    /**
     * 存储验证码信息
     * @param phoneNumber 手机号码
     * @param smsCode 验证码
     * @param bizId 发送回执id
     * @param sendTime 发送时间
     * @return BaseResponse<Any?>
     */
    @POST("/sms/save")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    suspend fun saveSmsData(
        @Field("phone_number") phoneNumber: String,
        @Field("sms_code") smsCode: String,
        @Field("biz_id") bizId: String,
        @Field("send_time") sendTime: String
    ): UserResponse<Any?>

    /**
     * 校验短信验证码
     * @param phoneNumber 手机号码
     * @param smsCode 验证码
     * @param bizId 发送回执id
     * @return BaseResponse<SmsResponse>
     */
    @POST("/sms/check")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    suspend fun checkSms(
        @Field("phone_number") phoneNumber: String,
        @Field("sms_code") smsCode: String,
        @Field("biz_id") bizId: String,
    ): UserResponse<SmsResponse?>

}