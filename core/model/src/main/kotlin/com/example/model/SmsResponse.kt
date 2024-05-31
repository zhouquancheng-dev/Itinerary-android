package com.example.model

import com.squareup.moshi.JsonClass

/**
 * @param status 状态
 * @param phoneNumber 手机号
 * @param bizId 发送回执id
 */
@JsonClass(generateAdapter = true)
data class SmsResponse(
    val status: Boolean,
    val phoneNumber: String,
    val bizId: String
)