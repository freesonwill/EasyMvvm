package com.xcjh.app.utils

import com.blankj.utilcode.util.TimeUtils
import com.hjq.language.MultiLanguages
import java.util.Calendar

object DateFormatUtil {

    private val monthMap = mapOf(
        Pair(Calendar.JANUARY, "January"),
        Pair(Calendar.FEBRUARY, "February"),
        Pair(Calendar.MARCH, "March"),
        Pair(Calendar.APRIL, "April"),
        Pair(Calendar.MAY, "May"),
        Pair(Calendar.JUNE, "June"),
        Pair(Calendar.JULY, "July"),
        Pair(Calendar.AUGUST, "August"),
        Pair(Calendar.SEPTEMBER, "September"),
        Pair(Calendar.OCTOBER, "October"),
        Pair(Calendar.NOVEMBER, "November"),
        Pair(Calendar.DECEMBER, "December")
    )

    fun formatLocalTime(
        date1: String?,
        date2: String? = "",
        pattern: String? = "yyyy-MM-dd HH:mm:ss"
    ): String {
        if(date1.isNullOrEmpty() && date2.isNullOrEmpty()){
            return ""
        }

        val language = MultiLanguages.getAppLanguage().language
        if (language == "zh") {
            val time1 = TimeUtils.string2Date(date1, pattern!!)
            return if (date2.isNullOrEmpty()) {
                TimeUtils.date2String(time1, "yyyy/MM/dd")
            } else {
                val time2 = TimeUtils.string2Date(date2, pattern)
                TimeUtils.date2String(time1, "yyyy/MM/dd") + "-" + TimeUtils.date2String(
                    time2,
                    "yyyy/MM/dd"
                )
            }
        } else {
            val time1 = TimeUtils.string2Date(date1, pattern!!)
            return if (date2.isNullOrEmpty()) {
                TimeUtils.date2String(time1, "MM/dd/yyyy")
            } else {
                val time2 = TimeUtils.string2Date(date2, pattern)
                TimeUtils.date2String(time1, "MM/dd/yyyy") + "-" + TimeUtils.date2String(
                    time2,
                    "MM/dd/yyyy"
                )
            }
        }
    }

    fun formatDate(
        startTime: String?,
        endTime: String?,
        pattern: String? = "yyyy-MM-dd HH:mm:ss"
    ): String {

        if (startTime.isNullOrEmpty() || endTime.isNullOrEmpty()) {
            return ""
        }

        val startDate = TimeUtils.string2Date(startTime, pattern!!)
        val endDate = TimeUtils.string2Date(endTime, pattern)

        val startYear = TimeUtils.getValueByCalendarField(startDate, Calendar.YEAR)
        val endYear = TimeUtils.getValueByCalendarField(endDate, Calendar.YEAR)
        val startMonth = TimeUtils.getValueByCalendarField(startDate, Calendar.MONTH)
        val endMonth = TimeUtils.getValueByCalendarField(endDate, Calendar.MONTH)
        var startDay =
            TimeUtils.getValueByCalendarField(startDate, Calendar.DAY_OF_MONTH).toString()
        if (startDay.length == 1) {
            startDay = "0$startDay"
        }
        var endDay = TimeUtils.getValueByCalendarField(endDate, Calendar.DAY_OF_MONTH).toString()
        if (endDay.length == 1) {
            endDay = "0$endDay"
        }

        if (startYear == endYear) {
            if (startMonth == endMonth) {//时间区间不跨月份：格式“May 02-07,2023”
                val month = monthMap[startMonth]
                return "$month $startDay-$endDay,$startYear"
            } else {//时间区间跨月份但不跨年份：格式“May 02 - June 07,2023”
                val startMon = monthMap[startMonth]
                val endMon = monthMap[endMonth]
                return "$startMon $startDay - $endMon $endDay,$startYear"
            }
        } else {//时间区间跨年份：格式“May 02,2022 - June 07,2023”
            val startMon = monthMap[startMonth]
            val endMon = monthMap[endMonth]

            return "$startMon $startDay,$startYear - $endMon $endDay,$endYear"
        }
    }

    fun getAfterMonth(today: String, month: Int): String {
        val calendar = Calendar.getInstance()
        val safeDateFormat = TimeUtils.getSafeDateFormat("yyyy-MM-dd")
        val date = safeDateFormat.parse(today)
        calendar.time = date
        calendar.add(Calendar.MONTH, month)
        return safeDateFormat.format(calendar.time)
    }

    fun getAfterDay(today: String, afterDay: Int):String{
        val calendar = Calendar.getInstance()
        val safeDateFormat = TimeUtils.getSafeDateFormat("yyyy-MM-dd")
        val date = safeDateFormat.parse(today)
        calendar.time = date
        calendar.add(Calendar.DATE, afterDay)
        return safeDateFormat.format(calendar.time)
    }
}