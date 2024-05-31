package com.example.network.interceptor

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

fun errorHandlingInterceptor(): Interceptor {
    return Interceptor { chain ->
        val response = chain.proceed(chain.request())
        if (!response.isSuccessful) {
            handleHttpError(response)
        }
        response
    }
}

private fun handleHttpError(response: Response) {
    when (response.code) {
        400 -> Log.e("HTTP Error", "Bad Request: ${response.message}")
        401 -> Log.e("HTTP Error", "Unauthorized: ${response.message}")
        403 -> Log.e("HTTP Error", "Forbidden: ${response.message}")
        404 -> Log.e("HTTP Error", "Not Found: ${response.message}")
        500 -> Log.e("HTTP Error", "Internal Server Error: ${response.message}")
        else -> Log.e("HTTP Error", "HTTP ${response.code}: ${response.message}")
    }
}