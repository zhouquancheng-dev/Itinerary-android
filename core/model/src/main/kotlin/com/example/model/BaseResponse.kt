package com.example.model

import kotlinx.serialization.Serializable

/**
 * @param status 返回状态码
 * @param msg 返回描述
 * @param data Data
 */
@Serializable
data class BaseResponse<T>(
    val status: Int,
    val msg: String,
    val data: T?
)