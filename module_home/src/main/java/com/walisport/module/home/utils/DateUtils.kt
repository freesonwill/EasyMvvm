package com.walisport.module.home.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object DateUtils {
    fun getNext7Days(): List<Pair<String, String>> {
        val dateList = mutableListOf<Pair<String, String>>()
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("MMdd", Locale.getDefault()) // MMDD 格式
        val weekdayFormat = SimpleDateFormat("E", Locale.CHINESE) // 取得星期幾 (中文)

        repeat(7) {
            val dateStr = dateFormat.format(calendar.time) // MMDD
            val weekdayStr = weekdayFormat.format(calendar.time) // 星期幾 (週一, 週二, ...)
            dateList.add(dateStr to weekdayStr)
            calendar.add(Calendar.DAY_OF_YEAR, 1) // 加一天
        }

        return dateList
    }

//    fun getNext7Days(): List<String> {
//        val dateList = mutableListOf<String>()
//        val calendar = Calendar.getInstance()
//        val dateFormat = SimpleDateFormat("MMdd", Locale.getDefault()) // 轉換為 MMDD 格式
//
//        repeat(7) { // 取得今天起未來 7 天
//            val dateStr = dateFormat.format(calendar.time) // MMDD
//            dateList.add(dateStr)
//            calendar.add(Calendar.DAY_OF_YEAR, 1) // 加一天
//        }
//
//        return dateList
//    }
}