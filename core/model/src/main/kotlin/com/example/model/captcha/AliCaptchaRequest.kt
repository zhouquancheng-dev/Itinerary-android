package com.example.model.captcha

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AliCaptchaRequest(
    @SerialName("lot_number") val lotNumber: String,
    @SerialName("captcha_output") val captchaOutput: String,
    @SerialName("pass_token") val passToken: String,
    @SerialName("gen_time") val genTime: String,
    @SerialName("captcha_id") val captchaId: String
)