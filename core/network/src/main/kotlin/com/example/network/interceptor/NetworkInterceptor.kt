package com.example.network.interceptor

import com.example.network.provider.AuthTokenProvider
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor

fun networkInterceptor(authTokenProvider: AuthTokenProvider): Interceptor {
    return Interceptor { chain ->
        val originalRequest = chain.request()

        val authToken = runBlocking { authTokenProvider.getAuthToken() }

        val requestBuilder = originalRequest.newBuilder()
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .method(originalRequest.method, originalRequest.body)

        if (authToken != null) {
            requestBuilder.addHeader("Authorization", "$authToken")
        }

        val request = requestBuilder.build()
        chain.proceed(request)
    }
}
