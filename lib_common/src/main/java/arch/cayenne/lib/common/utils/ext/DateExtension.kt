package arch.cayenne.lib.common.utils.ext

import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.base.utils.LogUtilsExt.loge
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private const val TAG = "DateExtension"
/**
 * @author: hamigua
 * @date: 2025/4/22 17:35
 * @description: 所有的扩展函数
 */
/**
 * 将Int类型的月份转换为中文月份
 * @receiver Int 月份
 * @return String 中文月份
 */
fun Int.toChineseMonth(): String {
    val months = listOf(
        "一月", "二月", "三月", "四月", "五月", "六月",
        "七月", "八月", "九月", "十月", "十一月", "十二月"
    )
    return if (this in 1..12) months[this - 1] else throw IllegalArgumentException("月份必须在1到12之间")
}

/**
 * 从字符串中提取年月日
 * @param pattern 日期格式，默认为"yyyyMMdd"
 */
fun String.extractDate(pattern: String = "yyyyMMdd"): Triple<Int, Int, Int>? {
    // 定义正则表达式，匹配8位数字的日期格式
    val dateRegex = Regex("^\\d{8}\$")

    // 检查输入是否符合日期格式
    if (!dateRegex.matches(this)) {
        "输入字符串不是有效的日期格式".loge(TAG)
        return null
    }
    return try {
        // 使用 SimpleDateFormat 验证日期是否有效
        val formatter = SimpleDateFormat(pattern, Locale.getDefault())
        formatter.isLenient = false
        val date = formatter.parse(this) ?: return null
        val calendar = Calendar.getInstance()
        calendar.time = date
        // 提取年、月、日
        Triple(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    } catch (e: ParseException) {
        e.printStackTrace()
        "日期无效: ${e.message}".loge(TAG)
        null
    }
}