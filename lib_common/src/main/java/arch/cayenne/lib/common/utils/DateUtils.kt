package arch.cayenne.lib.common.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 *
 * @date: 2025/8/9 15:46
 * @description:
 */
object DateUtils {
    // 将时间戳格式化为指定格式的日期时间字符串，基于用户当前时区
    // @param timestamp 以毫秒为单位的时间戳
    // @param dateFormat 指定的日期时间格式，例如 "yyyy-MM-dd HH:mm:ss"
    // @return 格式化后的日期时间字符串
    fun getDisplayStr(timestamp: Long, dateFormat: String): String {
        // 获取用户当前时区
        val timeZone = TimeZone.getDefault()
        // 创建 SimpleDateFormat 实例，用于格式化时间
        val timeFormatter = SimpleDateFormat(dateFormat, Locale.getDefault()).apply {
            this.timeZone = timeZone
        }

        // 将时间戳转换为用户时区时间
        val date = Date(timestamp)
        val dateTime = timeFormatter.format(date)

        return dateTime

    }
}