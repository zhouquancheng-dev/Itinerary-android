package com.example.model.captcha

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AliCaptchaResponse(
    val status: String,
    val result: String,
    val reason: String,
    @SerialName("captcha_args") val captchaArgs: CaptchaArgs?
) {
    @Serializable
    data class CaptchaArgs(
        @SerialName("used_type") val usedType: String?,
        @SerialName("user_ip") val userIp: String?,
        @SerialName("lot_number") val lotNumber: String?,
        val scene: String?,
        val referer: String?
    )
}
