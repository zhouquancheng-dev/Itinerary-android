package com.example.network

import com.example.common.BaseApplication
import com.example.common.connect.ConnectivityManagerNetworkMonitor
import com.example.network.adapter.DateAdapter
import com.example.network.interceptor.CacheInterceptor
import com.example.network.interceptor.errorHandlingInterceptor
import com.example.network.interceptor.loggingInterceptor
import com.example.network.okhttp.OkHttpDns
import com.example.network.url.APP_BASE_URL
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

object ServiceCreator {

    private const val CONNECT_TIMEOUT = 15L
    private const val READ_TIMEOUT = 10L

    private val networkMonitor by lazy { ConnectivityManagerNetworkMonitor() }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .dns(OkHttpDns(BaseApplication.getInstance()))
            .addInterceptor(loggingInterceptor())
            .addInterceptor(errorHandlingInterceptor())
            .addNetworkInterceptor(CacheInterceptor(networkMonitor))
            .cache(cache())
            .build()
    }

    private val json = Json {
        prettyPrint = true
        isLenient = true
        explicitNulls = false // 为 false 时序列化时忽略null
        ignoreUnknownKeys = true // 忽略未知键
    }
    private val contentType = "application/json".toMediaType()

    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .add(DateAdapter())
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(APP_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    fun <T> createRequest(serviceClass: Class<T>, baseUrl: String): T =
        retrofit.newBuilder().baseUrl(baseUrl).build().create(serviceClass)

    inline fun <reified T> createRequestApi(baseUrl: String = APP_BASE_URL): T =
        createRequest(T::class.java, baseUrl)

    private fun cache(): Cache {
        val cacheSize = 50L * 1024L * 1024L // 50 MB
        return Cache(File(BaseApplication.getInstance().cacheDir, "http_cache"), cacheSize)
    }

    fun clearCache() {
        try {
            okHttpClient.cache?.evictAll()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
