package com.example.network.interceptor

import com.example.common.connect.NetworkMonitor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class CacheInterceptor(
    private val networkMonitor: NetworkMonitor
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val modifiedRequest = runBlocking {
            modifyRequestBasedOnNetwork(chain.request())
        }
        return chain.proceed(modifiedRequest)
    }

    private suspend fun modifyRequestBasedOnNetwork(request: Request): Request {
        val isOffline = withContext(Dispatchers.IO) {
            networkMonitor.isOnline
                .map(Boolean::not)
                .first()
        }

        return if (isOffline) {
            // 在线状态，使用缓存数据，最大缓存时间为 5秒
            request.newBuilder()
                .header("Cache-Control", "public, max-age=5")
                .build()
        } else {
            // 离线状态，使用缓存数据，缓存数据最大有效期为 3天
            request.newBuilder()
                .header("Cache-Control", "public, only-if-cached, max-stale=${60 * 60 * 24 * 3}")
                .build()
        }
    }
}