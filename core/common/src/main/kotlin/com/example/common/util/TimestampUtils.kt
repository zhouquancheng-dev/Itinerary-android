package com.example.common.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object TimestampUtils {

    private val hourMinuteFormat = DateTimeFormatter.ofPattern("H:mm")
    private val monthDayFormat = DateTimeFormatter.ofPattern("M月d日")
    private val yearMonthDayFormat = DateTimeFormatter.ofPattern("yyyy年M月d日")

    fun formatTimestamp(timestamp: Long): String {
        val now = LocalDateTime.now()
        // 将秒级时间戳转换为 Instant 对象
        // 然后通过 atZone(ZoneId.systemDefault()) 转换为当前时区的 LocalDateTime
        val targetDateTime = Instant.ofEpochSecond(timestamp).atZone(ZoneId.systemDefault()).toLocalDateTime()
        val targetDate = targetDateTime.toLocalDate()

        // 判断是否为同一天
        if (targetDate.isEqual(now.toLocalDate())) {
            return targetDateTime.format(hourMinuteFormat)
        }

        // 判断是否为昨天
        if (targetDate.isEqual(now.minusDays(1).toLocalDate())) {
            return "昨天"
        }

        // 判断是否在一周内
        if (targetDate.isAfter(now.minusWeeks(1).toLocalDate())) {
            return "周" + arrayOf("日", "一", "二", "三", "四", "五", "六")[targetDate.dayOfWeek.value % 7]
        }

        // 判断是否在同一个月
        if (targetDate.month == now.month && targetDate.year == now.year) {
            return targetDate.format(monthDayFormat)
        }

        // 判断是否在同一年
        if (targetDate.year == now.year) {
            return targetDate.format(monthDayFormat)
        }

        // 超出当年
        return targetDate.format(yearMonthDayFormat)
    }

    // 判断两个日期是否为同一天
    private fun isSameDay(date1: LocalDate, date2: LocalDate): Boolean {
        return date1.isEqual(date2)
    }

}