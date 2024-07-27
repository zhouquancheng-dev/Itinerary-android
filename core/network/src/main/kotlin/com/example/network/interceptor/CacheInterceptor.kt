package com.example.network.interceptor

import com.example.common.di.ApplicationScope
import com.example.common.di.network.NetworkMonitor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CacheInterceptor @Inject constructor(
    @ApplicationScope private val appScope: CoroutineScope,
    private val networkMonitor: NetworkMonitor
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val isOffline = runBlocking {
            networkMonitor.isOnline
                .map(Boolean::not)
                .stateIn(appScope, SharingStarted.Eagerly, false)
                .first()
        }

        val modifiedRequest = if (isOffline) {
            // 离线状态，使用缓存数据，缓存数据最大有效期为 3天
            request.newBuilder()
                .header("Cache-Control", "public, only-if-cached, max-stale=${60 * 60 * 24 * 3}")
                .build()
        } else {
            // 在线状态，使用缓存数据，最大缓存时间为 5秒
            request.newBuilder()
                .header("Cache-Control", "public, max-age=5")
                .build()
        }

        return chain.proceed(modifiedRequest)
    }
}
