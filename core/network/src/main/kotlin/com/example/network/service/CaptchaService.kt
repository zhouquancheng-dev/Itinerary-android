package com.example.network.service

import com.example.model.captcha.AliCaptchaRequest
import com.example.model.captcha.AliCaptchaResponse
import com.example.network.url.ALI_CAPTCHA
import retrofit2.http.Body
import retrofit2.http.POST

interface CaptchaService {

    /**
     * 阿里云行为验证码二次校验
     *
     * {
     *     "lot_number": "",
     *     "captcha_output":"",
     *     "pass_token": "",
     *     "gen_time": "",
     *     "captcha_id": ""
     * }
     */
    @POST(ALI_CAPTCHA)
    suspend fun verifyCaptcha(
        @Body request: AliCaptchaRequest
    ): AliCaptchaResponse

}