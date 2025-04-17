package arch.cayenne.lib.common.utils.ext

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * 字符串扩展
 */
object StringExt {

    /**
     * 安全截取字符串
     */
    fun String.safeSubstring(start: Int, len: Int): String {
        return if (start == 0 && len == length) this
        else this.substring(start.coerceAtMost(length), (start+len).coerceAtMost(length))
    }

    fun String.toValue(): Int {
        if (this == "0") return 0 // 明確處理 0

        val value = if (this.last() == '.') {
            this.substring(0, this.length - 1)
        } else {
            this
        }
        return try {
            val decimal = BigDecimal(value).setScale(2, RoundingMode.DOWN)
            decimal.multiply(BigDecimal(100)).toInt()
        } catch (e: NumberFormatException) {
            0 // 或依需求處理錯誤情況
        }
    }
}