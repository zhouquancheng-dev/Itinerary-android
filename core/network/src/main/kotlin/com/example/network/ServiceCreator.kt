package com.example.network

import com.example.network.url.APP_BASE_URL
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.io.IOException
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class ServiceCreator @Inject constructor(
    private val retrofit: Retrofit,
    @Named("okhttpClient") private val okHttpClient: OkHttpClient
) {

    // 普通泛型函数，用于创建 API 服务实例
    // 内联函数会在编译时替换调用点的代码，可能会导致注解处理器无法正确处理它们，会编译报错，
    // 尽量避免在需要注解处理的地方使用内联函数
    fun <T> createRequestApi(service: Class<T>, baseUrl: String = APP_BASE_URL): T {
        return retrofit.newBuilder()
            .baseUrl(baseUrl)
            .build()
            .create(service)
    }

    // 清理请求缓存的方法
    fun clearCache() {
        try {
            okHttpClient.cache?.evictAll()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}
