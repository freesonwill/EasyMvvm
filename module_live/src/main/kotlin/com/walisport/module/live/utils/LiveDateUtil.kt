package com.walisport.module.live.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object LiveDateUtil {

    @SuppressLint("SimpleDateFormat")
    fun getMDHm(time: Long): String {
        var date: String = ""
        try {
            val sdf = SimpleDateFormat("MM月dd日 HH:mm")
            val dat = Date(time)
            date = sdf.format(dat)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return date
    }

    /**
     * 将时间戳转换为用户所在对应的时间，样式为日期-小时:分， 如果是当前这天， 日期显示为“今天”， 如果是明天， 则日期显示为“明天”
     */
    fun getDisplay(timestamp: Long): Pair<String, String> {
        // 获取用户当前时区
        val timeZone = TimeZone.getDefault()

        // 创建 SimpleDateFormat 实例，用于格式化时间
        val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault()).apply {
            this.timeZone = timeZone
        }
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
            this.timeZone = timeZone
        }

        // 将时间戳转换为用户时区时间
        val date = Date(timestamp)
        val timePart = timeFormatter.format(date)

        // 获取当前时间用于比较
        val now = Date()
        val calendarNow = Calendar.getInstance(timeZone).apply { time = now }
        val calendarInput = Calendar.getInstance(timeZone).apply { time = date }

        // 比较年、月、日以确定日期部分
        val isSameDay = calendarInput.get(Calendar.YEAR) == calendarNow.get(Calendar.YEAR) &&
                calendarInput.get(Calendar.MONTH) == calendarNow.get(Calendar.MONTH) &&
                calendarInput.get(Calendar.DAY_OF_MONTH) == calendarNow.get(Calendar.DAY_OF_MONTH)

        val isTomorrow = calendarInput.get(Calendar.YEAR) == calendarNow.get(Calendar.YEAR) &&
                calendarInput.get(Calendar.MONTH) == calendarNow.get(Calendar.MONTH) &&
                calendarInput.get(Calendar.DAY_OF_MONTH) == calendarNow.get(Calendar.DAY_OF_MONTH) + 1

        // 确定日期部分
        val datePart = when {
            isSameDay -> "今天"
            isTomorrow -> "明天"
            else -> dateFormatter.format(date)
        }

        // 返回格式化结果
        return Pair(datePart, timePart)
    }

}