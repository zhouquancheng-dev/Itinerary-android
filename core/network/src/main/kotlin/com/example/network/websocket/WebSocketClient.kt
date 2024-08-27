package com.example.network.websocket

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.network.url.WEBSOCKET_BASE_URL
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.security.KeyStore
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager
import kotlin.math.pow

/**
 * WebSocket 客户端
 *
 * @param owner LifecycleOwner 事件订阅的生命周期所有者
 * @param baseUrl WebSocket baseUrl
 * @param endpoint WebSocket endpoint
 * @param reconnectStrategy 重连策略
 * @param listener WebSocket 事件监听器
 */
class WebSocketClient(
    private val owner: LifecycleOwner,
    private val baseUrl: String = WEBSOCKET_BASE_URL,
    private val endpoint: String,
    private val reconnectStrategy: ReconnectStrategy = ReconnectStrategy(),
    private val listener: WebSocketEventListener
) : DefaultLifecycleObserver {

    private val sslSocketFactory: SSLSocketFactory
    private val trustManager: X509TrustManager

    companion object {
        const val HEARTBEAT_INTERVAL = 30 // 心跳消息间隔，单位：秒
    }

    init {
        createSSLConfiguration().apply {
            sslSocketFactory = first
            trustManager = second
        }
    }

    private val url: String get() = "$baseUrl/$endpoint"

    // 创建 SSL/TLS 配置
    private fun createSSLConfiguration(): Pair<SSLSocketFactory, X509TrustManager> {
        val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(null as KeyStore?)

        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustManagerFactory.trustManagers, null)

        val trustManager = trustManagerFactory.trustManagers[0] as X509TrustManager
        return sslContext.socketFactory to trustManager
    }

    // OkHttpClient 配置
    private val okHttpClient = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS) // 保持连接
        .sslSocketFactory(sslSocketFactory, trustManager)
        .retryOnConnectionFailure(true) // 支持重连
        .build()

    private val lifecycleScope = owner.lifecycleScope

    private var webSocket: WebSocket? = null
    private var isConnected = false
    private var reconnectAttempt = 0
    private var heartbeatJob: Job? = null // 心跳任务

    private fun createWebSocket() {
        val request = Request.Builder().url(url).build()
        webSocket = okHttpClient.newWebSocket(request, webSocketListener)
    }

    // WebSocket 监听器
    private val webSocketListener = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            isConnected = true
            reconnectAttempt = 0
            listener.onOpen(response)
            startHeartbeat()
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            listener.onMessage(text)
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            listener.onBinaryMessage(bytes)
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Log.e("WebSocketClient", "WebSocket error: ${t.message}", t)
            isConnected = false
            reconnect()
            listener.onFailure(t, response)
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            isConnected = false
            listener.onClosing(code, reason)
        }
    }

    // 连接 WebSocket
    private fun connect() {
        if (isConnected) return
        try {
            createWebSocket()
        } catch (e: Exception) {
            Log.e("WebSocketClient", "Error connecting WebSocket: ${e.message}", e)
            reconnect() // 连接失败时重连
        }
    }

    // 重连 WebSocket
    fun reconnect() {
        if (reconnectAttempt >= reconnectStrategy.maxAttempts) {
            Log.e("WebSocketClient", "Max reconnect attempts reached")
            return
        }
        val delayTime = reconnectStrategy.initialInterval *
                reconnectStrategy.backoffFactor.pow(reconnectAttempt.toDouble()) * 1000

        lifecycleScope.launch {
            delay(delayTime.toLong())
            reconnectAttempt++
            connect()
        }
    }

    // 发送心跳消息
    private fun startHeartbeat() {
        if (heartbeatJob == null || heartbeatJob?.isCancelled == true) {
            heartbeatJob = lifecycleScope.launch {
                while (isConnected) {
                    webSocket?.send("heartbeat") // 发送心跳消息
                    delay(HEARTBEAT_INTERVAL * 1000L) // 心跳消息间隔
                }
            }
        }
    }

    // 关闭 WebSocket
    private fun close() {
        heartbeatJob?.cancel() // 取消心跳任务
        webSocket?.close(1000, "Client closing connection")
        webSocket = null
        reconnectAttempt = 0
        isConnected = false
    }

    // 发送文本消息
    fun sendTextMessage(message: String) {
        if (isConnected) {
            webSocket?.send(message)
        }
    }

    // 发送二进制消息
    fun sendBinaryMessage(data: ByteString) {
        if (isConnected) {
            webSocket?.send(data)
        }
    }

    override fun onStart(owner: LifecycleOwner) {
        connect()
    }

    override fun onStop(owner: LifecycleOwner) {
        close()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        lifecycleScope.cancel()
    }
}
