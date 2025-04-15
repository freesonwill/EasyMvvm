package arch.cayenne.lib.common.utils.ext

import java.math.BigDecimal
import java.math.RoundingMode

object IntExt {

    fun Int.getMoney(): String {
        val value = this.toBigDecimal().divide(BigDecimal(100))
            .setScale(2, RoundingMode.DOWN)

        return if (value.stripTrailingZeros().scale() <= 0) {
            value.toPlainString().split(".")[0] // 顯示整數
        } else {
            value.stripTrailingZeros().toPlainString() // 去除多餘 0
        }
    }

    fun Int.getMoney(multiply: Int): String {
        val result = this * multiply
        val decimal = BigDecimal(result).divide(BigDecimal(100))
            .setScale(2, RoundingMode.DOWN)
        return if (decimal.stripTrailingZeros().scale() <= 0) {
            decimal.toPlainString().split(".")[0] // 僅整數部分
        } else {
            decimal.stripTrailingZeros().toPlainString()
        }
    }

    fun Int.getRate(): String {
        val rate = this / 100f
        val adjusted = if (rate < 0.1f) 0.1f else rate
        return String.format("%.2f", adjusted)
    }

    fun Int.getRate(multiply: Int): String {
        val result = this * multiply
        val decimal = BigDecimal(result).divide(BigDecimal(100))
        return decimal.setScale(2, RoundingMode.DOWN).toPlainString()
    }
}