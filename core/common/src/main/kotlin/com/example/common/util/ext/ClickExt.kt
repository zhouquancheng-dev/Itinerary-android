package com.example.common.util.ext

object ClickExt {
    private const val MIN_DELAY: Long = 300
    private var lastClickTime: Long = 0

    /**
     * 如果在 minInterval 之内的点击返回 true，否则返回 false
     */
    fun isFastClick(minInterval: Long = MIN_DELAY): Boolean {
        require(minInterval > 0) { "minInterval cannot be negative" }
        val now = System.currentTimeMillis()
        val isFast = (now - lastClickTime) <= minInterval
        lastClickTime = now
        return isFast
    }
}
