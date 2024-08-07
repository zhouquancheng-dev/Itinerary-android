package com.example.common.util

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object TimestampUtils {

    private val hourMinuteFormat = DateTimeFormatter.ofPattern("H:mm")
    private val monthDayFormat = DateTimeFormatter.ofPattern("M月d日")
    private val yearMonthDayFormat = DateTimeFormatter.ofPattern("yyyy年M月d日")
    private val dayOfWeekArray = arrayOf("日", "一", "二", "三", "四", "五", "六")

    fun formatTimestamp(timestamp: Long): String {
        val now = LocalDateTime.now()
        // 将秒级时间戳转换为 Instant 对象
        // 然后通过 atZone(ZoneId.systemDefault()) 转换为当前时区的 LocalDateTime
        val targetDateTime = Instant.ofEpochSecond(timestamp).atZone(ZoneId.systemDefault()).toLocalDateTime()
        val targetDate = targetDateTime.toLocalDate()

        val daysDifference = ChronoUnit.DAYS.between(targetDate, now.toLocalDate())

        return when {
            daysDifference == 0L -> targetDateTime.format(hourMinuteFormat)
            daysDifference == 1L -> "昨天"
            daysDifference in 2..6 -> "周" + dayOfWeekArray[targetDate.dayOfWeek.value % 7]
            targetDate.year == now.year -> targetDate.format(monthDayFormat)
            else -> targetDate.format(yearMonthDayFormat)
        }
    }
}
