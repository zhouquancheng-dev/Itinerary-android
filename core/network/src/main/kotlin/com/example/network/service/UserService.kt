package com.example.network.service

import com.example.model.sms.TokenVerifyRequest
import com.example.model.sms.TokenVerifyResponse
import com.example.model.Response
import com.example.model.captcha.AliCaptchaRequest
import com.example.model.captcha.AliCaptchaResponse
import com.example.model.oss.StsResponse
import com.example.model.sms.CheckSmsResponse
import com.example.model.sms.SendSmsResponse
import com.example.network.url.ALI_CAPTCHA
import com.example.network.url.ALI_SEND_CODE
import com.example.network.url.ALI_VERIFY_CODE
import com.example.network.url.JG_AUTH_LOGIN
import com.example.network.url.OSS_STS_TOKEN
import com.example.network.url.TIM_USER_SIG
import com.example.network.url.UPLOAD
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface UserService {

    /**
     * 极光一键登录验证
     *
     * @param request Body
     * ```
     * {
     *    "loginToken": "",
     *    "exID": null
     * }
     * ```
     */
    @POST(JG_AUTH_LOGIN)
    suspend fun loginTokenVerify(
        @Body request: TokenVerifyRequest
    ): Response<TokenVerifyResponse>

    /**
     * 发送验证码
     *
     * @param phoneNumber 手机号
     * @param codeLength 验证码长度，支持4-8位，默认值：6位
     * @param validTime 验证码有效时长，默认值：300s
     * @param sendInterval 发送时间间隔，默认值：60s
     */
    @POST(ALI_SEND_CODE)
    @FormUrlEncoded
    suspend fun sendSmsCode(
        @Field("phoneNumber") phoneNumber: String,
        @Field("codeLength") codeLength: Long = 6L,
        @Field("validTime") validTime: Long = 300L,
        @Field("sendInterval") sendInterval: Long = 60L,
    ): Response<SendSmsResponse>

    /**
     * 验证码核验
     *
     * @param phoneNumber 手机号
     * @param verifyCode 验证码
     */
    @POST(ALI_VERIFY_CODE)
    @FormUrlEncoded
    suspend fun verifySmsCode(
        @Field("phoneNumber") phoneNumber: String,
        @Field("verifyCode") verifyCode: String
    ): Response<CheckSmsResponse>

    /**
     * 阿里云行为验证码二次校验
     *
     * @param request Body
     * ```
     * {
     *     "lot_number": "",
     *     "captcha_output": "",
     *     "pass_token": "",
     *     "gen_time": "",
     *     "captcha_id": ""
     * }
     * ```
     */
    @POST(ALI_CAPTCHA)
    suspend fun verifyCaptcha(
        @Body request: AliCaptchaRequest
    ): AliCaptchaResponse

    /**
     * 获取TIM登录票据
     */
    @GET(TIM_USER_SIG)
    suspend fun getUserSig(@Query("userId") userId: String): Response<String?>

    /**
     * 获取OSS STS临时凭证
     */
    @GET(OSS_STS_TOKEN)
    suspend fun getStsToken(): Response<StsResponse>

    /**
     * 上传文件
     */
    @Multipart
    @POST(UPLOAD)
    suspend fun uploadFile(
        @Part file: MultipartBody.Part,
        @Part("bucketDirName") bucketDirName: RequestBody,
        @Part("fileName") fileName: RequestBody
    ): Response<String?>

}