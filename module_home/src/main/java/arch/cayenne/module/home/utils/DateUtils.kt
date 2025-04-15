package arch.cayenne.module.home.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object DateUtils {
    fun getFutureDays(days: Int, locale: Locale): List<Pair<String, String>> {
        val dateList = mutableListOf<Pair<String, String>>()
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("MMdd", Locale.getDefault()) // MMDD 格式
        val weekdayFormat = SimpleDateFormat("E", locale) // TODO 取得星期幾 (中文, 未來再因應多語系修改)

        repeat(days) {
            val dateStr = dateFormat.format(calendar.time) // MMDD
            val weekdayStr = weekdayFormat.format(calendar.time) // 星期幾 (週一, 週二, ...)
            dateList.add(dateStr to weekdayStr)
            calendar.add(Calendar.DAY_OF_YEAR, 1) // 加一天
        }

        return dateList
    }
}