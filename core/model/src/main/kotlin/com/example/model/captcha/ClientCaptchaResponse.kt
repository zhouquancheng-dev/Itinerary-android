package com.example.model.captcha

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SuccessResponse(
    @SerialName("lot_number") val lotNumber: String,
    @SerialName("pass_token") val passToken: String,
    @SerialName("gen_time") val genTime: String,
    @SerialName("captcha_output") val captchaOutput: String
)

@Serializable
data class FailureResponse(
    @SerialName("captchaId") val captchaId: String,
    @SerialName("captchaType") val captchaType: String,
    @SerialName("challenge") val challenge: String
)

@Serializable
data class ErrorResponse(
    val code: String,
    val msg: String,
    val desc: Description
) {
    @Serializable
    data class Description(
        val description: String
    )
}