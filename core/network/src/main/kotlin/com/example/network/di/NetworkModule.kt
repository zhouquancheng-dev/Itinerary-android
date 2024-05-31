package com.example.network.di

import com.example.common.BaseApplication
import com.example.common.config.AppConfig
import com.example.common.connect.NetworkMonitor
import com.example.network.adapter.DateAdapter
import com.example.network.interceptor.CacheInterceptor
import com.example.network.interceptor.errorHandlingInterceptor
import com.example.network.interceptor.networkInterceptor
import com.example.network.provider.AuthTokenProvider
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        logging.level = if (AppConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
        return logging
    }

    @Provides
    @Singleton
    fun provideCache(): Cache {
        val cacheSize = 50L * 1024L * 1024L // 50 MB
        return Cache(File(BaseApplication.getContext().cacheDir, "http_cache"), cacheSize)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        networkMonitor: NetworkMonitor,
        authTokenProvider: AuthTokenProvider,
        cache: Cache
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .addNetworkInterceptor(CacheInterceptor(networkMonitor))
            .addNetworkInterceptor(networkInterceptor(authTokenProvider))
            .addInterceptor(errorHandlingInterceptor())
            .cache(cache)
            .build()
    }

    @ExperimentalSerializationApi
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val contentType = "application/json".toMediaType()
        val json = Json {
            prettyPrint = true
            isLenient = true
            explicitNulls = false
            ignoreUnknownKeys = true
        }

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .add(DateAdapter())
            .build()

        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl("https://your.api.base.url")
            .build()
    }
}
