package com.example.model

import com.squareup.moshi.JsonClass

/**
 * @param code 返回状态码
 * @param msg 状态说明
 * @param data data
 */
@JsonClass(generateAdapter = true)
data class UserResponse<T>(
    val code: Int,
    val msg: String,
    val data: T? = null
)