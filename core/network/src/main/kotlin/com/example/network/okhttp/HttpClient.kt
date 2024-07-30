package com.example.network.okhttp

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

object HttpClient {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private val json = Json { ignoreUnknownKeys = true }

    fun <T> get(
        url: String,
        responseSerializer: KSerializer<T>,
        callback: HttpCallback<T>
    ) {
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onError(e.message ?: "Unknown error")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseData = response.body.string()
                    runCatching {
                        json.decodeFromString(responseSerializer, responseData)
                    }.onSuccess { deserializedData ->
                        callback.onSuccess(deserializedData)
                    }.onFailure { e ->
                        callback.onError("Deserialization error: ${e.message}")
                    }
                } else {
                    callback.onError("Request failed: ${response.message}")
                }
            }
        })
    }

    fun post(
        url: String,
        jsonBody: String? = null,
        callback: HttpCallback<String>
    ) {
        val requestBody = jsonBody?.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
            ?: "".toRequestBody("text/plain; charset=utf-8".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onError(e.message ?: "Unknown error")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    callback.onSuccess(response.body.string())
                } else {
                    callback.onError("Request failed: ${response.message}")
                }
            }
        })
    }

}
