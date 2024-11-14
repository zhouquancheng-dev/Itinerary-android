package com.example.common.interceptor

import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Response

class RequestHeaderInterceptor(
    private val name: String,
    private val value: String,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val headers = Headers.Builder()
            .set(name, value)
            .build()
        val request = chain.request().newBuilder()
            .headers(headers)
            .build()
        return chain.proceed(request)
    }
}