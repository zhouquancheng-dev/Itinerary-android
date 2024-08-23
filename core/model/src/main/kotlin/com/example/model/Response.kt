package com.example.model

import kotlinx.serialization.Serializable

/**
 * @param status 返回状态码
 * @param msg 返回描述
 * @param data Data
 */
@Serializable
data class Response<T>(
    val status: Int,
    val msg: String,
    val data: T?
)

const val SUCCESS_STR = "success"

const val SUCCESS = 200

const val FAILURE = 401

const val SERVER_ERROR = 500