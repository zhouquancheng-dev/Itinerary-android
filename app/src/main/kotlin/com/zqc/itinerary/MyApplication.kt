package com.zqc.itinerary

import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.annotation.ExperimentalCoilApi
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.gif.AnimatedImageDecoder
import coil3.gif.GifDecoder
import coil3.memory.MemoryCache
import coil3.network.CacheStrategy
import coil3.network.ConnectivityChecker
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.CachePolicy
import coil3.svg.SvgDecoder
import coil3.util.DebugLogger
import coil3.video.VideoFrameDecoder
import com.example.common.BaseApplication
import com.example.common.config.AppConfig
import com.example.common.interceptor.RequestHeaderInterceptor
import dagger.hilt.android.HiltAndroidApp
import okhttp3.OkHttpClient

@HiltAndroidApp
class MyApplication : BaseApplication(), SingletonImageLoader.Factory {

    @OptIn(ExperimentalCoilApi::class)
    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader.Builder(context)
            .components {
                add(
                    OkHttpNetworkFetcherFactory(
                        cacheStrategy = { CacheStrategy.DEFAULT },
                        callFactory = {
                            OkHttpClient.Builder()
                                .addInterceptor(RequestHeaderInterceptor("Cache-Control", "no-cache"))
                                .build()
                        },
                        connectivityChecker = { ctx -> ConnectivityChecker(ctx) }
                    )
                )
                add(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        AnimatedImageDecoder.Factory()
                    } else {
                        GifDecoder.Factory()
                    }
                )
                add(SvgDecoder.Factory())
                add(VideoFrameDecoder.Factory())
            }
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(context, 0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(this.cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.02)
                    .build()
            }
            .networkCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .logger(
                if (AppConfig.DEBUG) {
                    DebugLogger()
                } else {
                    null
                }
            )
            .build()
    }

    override fun isDebug(): Boolean {
        return BuildConfig.DEBUG
    }

    @AppCompatDelegate.NightMode
    override fun getSystemNightMode(): Int {
        return AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    }

    override fun initData() {
        super.initData()
        initAppConfig()
    }

    /**
     * 初始化App的配置信息
     */
    private fun initAppConfig() {
        AppConfig.DEBUG = BuildConfig.DEBUG
        AppConfig.APP_NAME = BuildConfig.APP_NAME
        AppConfig.APPLICATION_ID = BuildConfig.APPLICATION_ID
        AppConfig.PRIVACY_URL = BuildConfig.PRIVACY_URL
        AppConfig.USER_PROTOCOL_URL = BuildConfig.USER_PROTOCOL_URL
        AppConfig.FILING_NO = BuildConfig.FILING_NO
        AppConfig.JIGUANG_APPKEY = BuildConfig.JIGUANG_APPKEY
        AppConfig.TENCENT_IM_APP_ID = BuildConfig.TENCENT_IM_APP_ID.toInt()
    }

}