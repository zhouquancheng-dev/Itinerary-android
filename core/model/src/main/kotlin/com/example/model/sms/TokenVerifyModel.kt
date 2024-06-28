package com.example.model.sms

import kotlinx.serialization.Serializable

@Serializable
data class TokenVerifyRequest(
    val loginToken: String,
    val exID: String?
)

@Serializable
data class TokenVerifyResponse(
    val id: Long?,
    val code: Int,
    val content: String,
    val exID: String?,
    val phone: String?
)