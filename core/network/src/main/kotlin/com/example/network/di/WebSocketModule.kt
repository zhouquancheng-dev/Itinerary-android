package com.example.network.di

import com.example.network.url.WEBSOCKET_BASE_URL
import com.example.network.websocket.ReconnectStrategy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import okhttp3.OkHttpClient
import java.security.KeyStore
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

@Module
@InstallIn(ActivityComponent::class)
object WebSocketModule {

    private const val CONNECT_TIMEOUT = 10_000L
    private const val READ_TIMEOUT = 30_000L

    @Provides
    @Singleton
    @Named("BaseUrl")
    fun provideBaseUrl(): String {
        return WEBSOCKET_BASE_URL
    }

    @Provides
    @Singleton
    @Named("websocketOkhttpClient")
    fun provideOkHttpClient(
        sslConfig: Pair<SSLSocketFactory, X509TrustManager>
    ): OkHttpClient {
        val (sslSocketFactory, trustManager) = sslConfig
        return OkHttpClient.Builder()
            .readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS) // 设置读取超时时间为30秒
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS) // 设置连接超时时间为10秒
            .sslSocketFactory(sslSocketFactory, trustManager)
            .retryOnConnectionFailure(true)
            .build()
    }

    @Provides
    @Singleton
    fun provideSSLConfiguration(): Pair<SSLSocketFactory, X509TrustManager> {
        val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(null as KeyStore?)

        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustManagerFactory.trustManagers, null)

        val trustManager = trustManagerFactory.trustManagers[0] as X509TrustManager
        return sslContext.socketFactory to trustManager
    }

    @Provides
    @Singleton
    fun provideReconnectStrategy(): ReconnectStrategy {
        return ReconnectStrategy(initialInterval = 10, maxAttempts = 5, backoffFactor = 2.0)
    }
}
