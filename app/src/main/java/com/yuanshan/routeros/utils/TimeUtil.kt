package com.yuanshan.routeros.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * 时间计算工具类
 */
object TimeUtil {

    private val timeZone = TimeZone.getTimeZone("GMT+8")
    private val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).apply {
        this.timeZone = this@TimeUtil.timeZone
    }

    /**
     * 获取当前时间，格式为 HH:mm:ss
     */
    fun getCurrentTimeRoundedToSecond(): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MILLISECOND, 0)
        return SimpleDateFormat("HH:mm:ss", Locale.getDefault()).apply {
            timeZone = this@TimeUtil.timeZone
        }.format(calendar.time)
    }

    /**
     * 获取当前时间戳，但毫秒数归零
     */
    fun getCurrentTimestampRoundedToSecond(): Long {
        val calendar = Calendar.getInstance()
        return calendar.timeInMillis
    }

    /**
     * 计算指定时间后的时间
     * @param seconds 秒数
     * @return 指定秒数后的时间，格式为 HH:mm:ss
     */
    fun getTimeAfterSeconds(seconds: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.SECOND, seconds)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return SimpleDateFormat("HH:mm:ss", Locale.getDefault()).apply {
            timeZone = this@TimeUtil.timeZone
        }.format(calendar.time)
    }

    /**
    * 计算某个特定时间后指定秒数的时间
    * @param baseTime 基准时间戳（毫秒）
    * @param seconds 秒数
    * @return 指定秒数后的时间，格式为 HH:mm:ss
    */
    fun getTimeAfterSecondsFromBase(baseTime: Long, seconds: Int): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = baseTime
        calendar.add(Calendar.SECOND, seconds)
        calendar.set(Calendar.MILLISECOND, 0)
        return SimpleDateFormat("HH:mm:ss", Locale.getDefault()).apply {
            timeZone = this@TimeUtil.timeZone
        }.format(calendar.time)
    }

    /**
     * 计算某个特定时间后指定秒数的时间戳
     * @param baseTime 基准时间戳（毫秒）
     * @param seconds 秒数
     * @return 指定秒数后的时间戳，秒数和毫秒数归零
     */
    fun getTimestampAfterSecondsFromBase(baseTime: Long, seconds: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = baseTime
        calendar.add(Calendar.SECOND, seconds)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    /**
     * 格式化时间戳为 HH:mm:ss 格式
     */
    fun formatTimestamp(timestamp: Long): String {
        return dateFormat.format(Date(timestamp))
    }

    /**
     * 格式化时间戳为 HH:mm:ss 格式
     */
    fun formatTimestampRoundedToMinute(timestamp: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return SimpleDateFormat("HH:mm:ss", Locale.getDefault()).apply {
            timeZone = this@TimeUtil.timeZone
        }.format(calendar.time)
    }
}