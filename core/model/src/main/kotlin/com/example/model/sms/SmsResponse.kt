package com.example.model.sms

import kotlinx.serialization.Serializable

@Serializable
data class SendSmsResponse(
    val accessDeniedDetail: String?,
    val code: String,
    val message: String,
    val model: ModelDetail,
    val success: String
) {
    @Serializable
    data class ModelDetail(
        val bizId: String,
        val outId: String?,
        val requestId: String,
        val verifyCode: String?
    )
}

@Serializable
data class CheckSmsResponse(
    val accessDeniedDetail: String?,
    val code: String,
    val message: String,
    val model: ModelDetail,
    val success: String
) {
    @Serializable
    data class ModelDetail(
        val outId: String?,
        val verifyResult: String
    )
}