package com.example.network.websocket

import okhttp3.Response
import okio.ByteString

/**
 * WebSocket 事件监听器接口
 */
interface WebSocketEventListener {
    fun onOpen(response: Response)
    fun onMessage(text: String)
    fun onBinaryMessage(bytes: ByteString)
    fun onFailure(t: Throwable, response: Response?)
    fun onClosing(code: Int, reason: String?)
}
