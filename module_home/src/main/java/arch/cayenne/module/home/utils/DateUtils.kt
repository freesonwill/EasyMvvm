package arch.cayenne.module.home.utils

import android.annotation.SuppressLint
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

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
    @SuppressLint("SimpleDateFormat")
    fun getMessageTime(timestamp: Long): String {
        val currentTime = System.currentTimeMillis()
        val diffTime = currentTime - timestamp
        val diffDays = TimeUnit.DAYS.convert(diffTime, TimeUnit.MILLISECONDS)
        "diffDays: $diffDays".logd()
        val date = Date(timestamp)
        val sdf = when (diffDays) {
            0L -> {
                val currentWeek = SimpleDateFormat("EE", Locale.getDefault()).format(currentTime)
                val messageWeek = SimpleDateFormat("EE", Locale.getDefault()).format(timestamp)
                val isSameWeek = currentWeek == messageWeek
                if (isSameWeek) {
                    SimpleDateFormat("HH:mm")
                } else {
                    SimpleDateFormat("EE")
                }
            }
            in 1..7 -> {
                SimpleDateFormat("EE")
            }
            else -> {
                SimpleDateFormat("M.dd HH:mm")
            }
        }
        return sdf.format(date).replace("週", "周")
    }
}