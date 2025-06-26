package arch.cayenne.module.home.utils

import android.text.format.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object DateUtils {
    fun getFutureDays(days: Int, locale: Locale): List<Triple<String, String, Long>> {
        val dateList = mutableListOf<Triple<String, String, Long>>()
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val dateFormat = SimpleDateFormat("M.dd", Locale.getDefault())
        val weekdayFormat = SimpleDateFormat("EEEE", locale)

        repeat(days) {
            val dateStr = dateFormat.format(calendar.time) // MMdd
            val weekdayStr = weekdayFormat.format(calendar.time) // 星期幾
            val timestamp = calendar.timeInMillis
            dateList.add(Triple(dateStr, weekdayStr, timestamp))
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        return dateList
    }
    fun getDate(timestamp: Long,dateFormat: String = "MMdd") :String {
        val calendar = Calendar.getInstance(Locale.ENGLISH)
        calendar.timeInMillis = timestamp
        val date = DateFormat.format(dateFormat,calendar).toString()
        return date
    }
}