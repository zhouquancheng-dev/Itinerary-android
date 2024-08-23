package com.example.network.okhttp

import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

object HttpClient {
    val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    val json = Json { ignoreUnknownKeys = true }

    inline fun <reified T> get(
        url: String,
        callback: HttpCallback<T>
    ) {
        require(url.isNotBlank()) { "URL cannot be blank" }

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
                        json.decodeFromString<T>(responseData)
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
        body: String? = null,
        contentType: String = "application/json; charset=utf-8",
        callback: HttpCallback<String>
    ) {
        require(url.isNotBlank()) { "URL cannot be blank" }

        when {
            contentType.contains("application/json") -> {
                require(body == null || isValidJson(body)) { "Invalid JSON format" }
            }
            contentType.contains("application/x-www-form-urlencoded") -> {
                require(body == null || isValidFormData(body)) { "Invalid form data format" }
            }
            // 可以添加更多的contentType格式检查
        }

        val requestBody = body?.toRequestBody(contentType.toMediaTypeOrNull())
            ?: "".toRequestBody(contentType.toMediaTypeOrNull())

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

    private fun isValidJson(json: String): Boolean {
        return try {
            JSONObject(json)
            true
        } catch (ex: JSONException) {
            try {
                JSONArray(json)
                true
            } catch (ex: JSONException) {
                false
            }
        }
    }

    private fun isValidFormData(formData: String): Boolean {
        return formData.split("&").all { pair ->
            pair.contains("=") && pair.split("=").size == 2
        }
    }

}
