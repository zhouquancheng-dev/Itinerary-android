package com.example.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CaptchaResponse(
    @Json(name = "captcha_id") val captchaId: String,
    @Json(name = "lot_number") val lotNumber: String,
    @Json(name = "pass_token") val passToken: String,
    @Json(name = "gen_time") val genTime: String,
    @Json(name = "captcha_output") val captchaOutput: String
)