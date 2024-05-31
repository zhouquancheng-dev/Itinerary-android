package com.example.common.util

object ClickUtils {
    private const val MIN_DELAY: Long = 300
    private var lastClickTime: Long = 0

    fun isFastClick(minInterval: Long = MIN_DELAY): Boolean {
        check(minInterval < 0) { "minInterval cannot be negative" }
        val now = System.currentTimeMillis()
        val isFast = (now - lastClickTime) <= minInterval
        lastClickTime = now
        return isFast
    }
}
