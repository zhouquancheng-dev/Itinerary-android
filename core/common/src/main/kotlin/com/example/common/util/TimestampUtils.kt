package com.example.common.util

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

object TimestampUtils {

    private val hourMinuteFormat = DateTimeFormatter.ofPattern("H:mm")
    private val monthDayFormat = DateTimeFormatter.ofPattern("M月d日")
    private val yearMonthDayFormat = DateTimeFormatter.ofPattern("yyyy年M月d日")

    fun formatTimestamp(timestamp: Long): String {
        val now = LocalDateTime.now()
        val nowDate = now.toLocalDate()

        // 将秒级时间戳转换为 Instant 对象
        val targetDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault())
        val targetDate = targetDateTime.toLocalDate()

        val daysDifference = nowDate.toEpochDay() - targetDate.toEpochDay()

        return when {
            daysDifference == 0L -> targetDateTime.format(hourMinuteFormat)
            daysDifference == 1L -> "昨天"
            daysDifference in 2..6 -> {
                val dayOfWeek = targetDate.dayOfWeek.getDisplayName(TextStyle.NARROW_STANDALONE, Locale("zh", "CN"))
                "周$dayOfWeek"
            }
            targetDate.year == now.year -> targetDate.format(monthDayFormat)
            else -> targetDate.format(yearMonthDayFormat)
        }
    }
}
