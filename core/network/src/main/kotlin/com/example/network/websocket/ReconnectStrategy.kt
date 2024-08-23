package com.example.network.websocket

/**
 * 定义 WebSocket 重连策略
 *
 * @param initialInterval 初始重连间隔，单位：秒
 * @param maxAttempts 最大重连次数
 * @param backoffFactor 指数退避系数
 */
data class ReconnectStrategy(
    val initialInterval: Long = 10,
    val maxAttempts: Int = 5,
    val backoffFactor: Double = 2.0
)
