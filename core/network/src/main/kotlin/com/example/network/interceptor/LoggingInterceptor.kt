package com.example.network.interceptor

import com.example.common.config.AppConfig
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor

fun loggingInterceptor(): Interceptor {
    val logging = HttpLoggingInterceptor()
    logging.level = if (AppConfig.DEBUG) {
        HttpLoggingInterceptor.Level.BODY
    } else  {
        HttpLoggingInterceptor.Level.NONE
    }
    return logging
}