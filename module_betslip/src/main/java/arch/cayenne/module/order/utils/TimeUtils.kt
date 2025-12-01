package arch.cayenne.module.order.utils

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object TimeUtils {

    fun formatTimeMillis(timeMillis: Long): String {
        val current = Calendar.getInstance()
        val target = Calendar.getInstance().apply { timeInMillis = timeMillis }

        val isSameYear = current.get(Calendar.YEAR) == target.get(Calendar.YEAR)

        // 自動依 Locale 顯示日期樣式
        val locale = Locale.getDefault()

        // 使用 DateFormat 取得本地化日期格式範本
        val fullFormat = (DateFormat.getDateInstance(DateFormat.LONG, locale) as SimpleDateFormat)
        val pattern = fullFormat.toPattern()

        // 根據是否同年，動態移除年份部分
        val finalPattern = if (isSameYear) {
            // 移除年 (y 或 yyyy)，只保留月日部分
            pattern.replace(Regex("[^Md/\\u4e00-\\u9fa5\\u1100-\\u11ff\\u3130-\\u318f\\uac00-\\ud7af\\s]+y+[^Md]*"), "")
                .replace(Regex("y+[^Md]*"), "")
                .trim { it <= ' ' }
        } else {
            pattern
        }

        val sdf = SimpleDateFormat(finalPattern, locale)
        return sdf.format(target.time)
    }

    /**
     * 获取时区的偏移小时，比如Beijing为UTC+8，返回8
     * @param timeZone，eg：TimeZone.getTimeZone("Asia/Shanghai")
     * @return
     */
    fun timeZoneOffsetHours(timeZone: TimeZone = TimeZone.getDefault()):Int {
        val offsetMillis = timeZone.rawOffset // 偏移毫秒
        val offsetHours = offsetMillis / (1000 * 60 * 60) // 转换为小时
        return offsetHours
    }
}