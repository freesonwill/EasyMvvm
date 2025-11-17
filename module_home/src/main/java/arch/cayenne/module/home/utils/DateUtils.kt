package arch.cayenne.module.home.utils

import android.annotation.SuppressLint
import android.app.Application
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.base.utils.log.Utils
import arch.cayenne.lib.common.utils.LanguageUtils
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.module.home.R
import org.koin.java.KoinJavaComponent.getKoin
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

object DateUtils {
    private val app: Application by lazy {
        // 占位对象，避免编辑器崩溃
        if (Utils.isInEditMode()) return@lazy Application()
        getKoin().get<Application>()
    }


    fun getFutureDays(
        days: Int,
        locale: Locale,
        firstDayTitle: String = "",
        dateFormatStr: String = "M.dd",
        weekdayFormatStr: String = "EEEE"
    ): List<Triple<String, String, Long>> {
        val dateList = mutableListOf<Triple<String, String, Long>>()
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val dateFormat = SimpleDateFormat(dateFormatStr, Locale.getDefault())
        val weekdayFormat = SimpleDateFormat(weekdayFormatStr, locale)
        var index = 0
        repeat(days) {
            val dateStr = dateFormat.format(calendar.time) // MMdd
            val weekdayStr =
                if (index == 0 && firstDayTitle.isNotEmpty()) firstDayTitle else weekdayFormat.format(
                    calendar.time
                ).replace("週", "周") // 星期幾
            val timestamp = calendar.timeInMillis
            dateList.add(Triple(dateStr, weekdayStr, timestamp))
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            index += 1
        }

        return dateList
    }

    /**
     * @param timestamp 当天任意时间的时间戳
     * 获取当天零点的timestamp
     */
    fun getMidnightTimeStamp(timestamp: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        return calendar.timeInMillis
    }

    fun getMonthDay(
        strDate: String,
        sourceDateFormat: String = "yyyyMMdd",
        targetDateFormat: String = "M.dd"
    ): String {
        val dateFormat = SimpleDateFormat(sourceDateFormat, Locale.getDefault())
        val selectedTime = dateFormat.parse(strDate)?.time
        val monthDayFormat = SimpleDateFormat(targetDateFormat, Locale.getDefault())
        return monthDayFormat.format(selectedTime ?: System.currentTimeMillis())
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

    /**
     * 将时间戳转换为用户所在对应的时间，样式为日期-星期， 如果是当前这天， 星期显示为“今天”， 如果是明天， 则星期显示为“明天”， 其余显示为“周*”
     */
    fun getDisplay(timestamp: Long): Pair<String, String> {
        // 获取用户当前时区
        val timeZone = TimeZone.getDefault()

        val sameYearDateFormatter = if (LanguageUtils.isChinese(app)) {
            SimpleDateFormat("MM月dd日", Locale.getDefault()).apply {
                this.timeZone = timeZone
            }
        } else {
            SimpleDateFormat("MM-dd", Locale.getDefault()).apply {
                this.timeZone = timeZone
            }
        }

        val dateFormatter = if (LanguageUtils.isChinese(app)) {
            SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault()).apply {
                this.timeZone = timeZone
            }
        } else {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
                this.timeZone = timeZone
            }
        }

        // 将时间戳转换为用户时区时间
        val date = Date(timestamp)

        // 获取当前时间用于比较
        val now = Date()
        val calendarNow = Calendar.getInstance(timeZone).apply { time = now }
        val calendarNextDay = Calendar.getInstance(timeZone).apply { time = now }
        //add方法会自动处理跨月或跨年的情况。例如，10月31日加一天会变成11月1日，12月31日加一天会变成下一年的1月1日。
        calendarNextDay.add(Calendar.DAY_OF_MONTH, 1)
        val calendarInput = Calendar.getInstance(timeZone).apply { time = date }

        // 比较年、月、日以确定日期部分
        val isSameDay = calendarInput.get(Calendar.YEAR) == calendarNow.get(Calendar.YEAR) &&
                calendarInput.get(Calendar.MONTH) == calendarNow.get(Calendar.MONTH) &&
                calendarInput.get(Calendar.DAY_OF_MONTH) == calendarNow.get(Calendar.DAY_OF_MONTH)

        val isTomorrow = calendarInput.get(Calendar.YEAR) == calendarNextDay.get(Calendar.YEAR) &&
                calendarInput.get(Calendar.MONTH) == calendarNextDay.get(Calendar.MONTH) &&
                calendarInput.get(Calendar.DAY_OF_MONTH) == calendarNextDay.get(Calendar.DAY_OF_MONTH)
        val dayOfWeek = calendarInput.get(Calendar.DAY_OF_WEEK)

        val isSameYear = calendarInput.get(Calendar.YEAR) == calendarNow.get(Calendar.YEAR)

        // 确定日期部分
        val datePart = when {
            isSameYear -> sameYearDateFormatter.format(date)
            else -> dateFormatter.format(date)
        }

        val weekPart = when {
            isSameDay -> R.string.match_date_today.getString()
            isTomorrow -> R.string.match_date_tomorrow.getString()
            else -> when (dayOfWeek) {
                0 -> R.string.match_date_saturday.getString()
                1 -> R.string.match_date_sunday.getString()
                2 -> R.string.match_date_monday.getString()
                3 -> R.string.match_date_tuesday.getString()
                4 -> R.string.match_date_wednesday.getString()
                5 -> R.string.match_date_thursday.getString()
                6 -> R.string.match_date_friday.getString()
                7 -> R.string.match_date_saturday.getString()
                else -> ""
            }
        }

        // 返回格式化结果
        return Pair(datePart, weekPart)
    }
}