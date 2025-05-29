package arch.cayenne.lib.common.utils.ext

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale

object SportIntExt {
    /**
     * @return string: 1234 轉換為 12.34, 1000 轉換為 10
     */
    fun Long.getMoney(): String {
        if (this == 0L) return "0" // ← 明確處理 0

        val value = this.toBigDecimal().divide(BigDecimal(100))
            .setScale(2, RoundingMode.DOWN)

        return if (value.stripTrailingZeros().scale() <= 0) {
            value.toPlainString().split(".")[0] // 顯示整數
        } else {
            value.stripTrailingZeros().toPlainString() // 去除多餘 0
        }
    }

    /***
     * @param multiply 乘數: 通常為賠率
     */
    fun Long.getMoney(multiply: Int): String {
        if (this == 0L || multiply == 0) return "0" // ← 明確處理 0

        val result = this * multiply
        val decimal = BigDecimal(result).divide(BigDecimal(10000))
            .setScale(2, RoundingMode.DOWN)
        return if (decimal.stripTrailingZeros().scale() <= 0) {
            decimal.toPlainString().split(".")[0] // 僅整數部分
        } else {
            decimal.stripTrailingZeros().toPlainString()
        }
    }

    /**
     * @return string: 123456 轉換為 1,234.56, 123456789 轉換為 1,234,567.89
     */
    fun Long.getFormalMoney(): String {
        if (this == 0L) return "0"

        val value = this.toBigDecimal().divide(BigDecimal(100)).setScale(2, RoundingMode.DOWN)
        val stripped = value.stripTrailingZeros()

        val numberFormat = NumberFormat.getNumberInstance(Locale.US).apply {
            maximumFractionDigits = 2
            minimumFractionDigits = if (stripped.scale() > 0) 2 else 0
            isGroupingUsed = true // 千分位
        }

        return numberFormat.format(value)
    }

    /**
     * @return string: 1234 轉換為 12.34, 1000 轉換為 10.00
     */
    fun Int.getOdds(): String {
        if (this == 0) return "0.00" // ← 明確處理 0

        val rate = this / 100f
        val adjusted = if (rate < 0.01f) 0.01f else rate
        return String.format("%.2f", adjusted)
    }

    fun Int.getOdds(multiply: Int): String {
        if (this == 0 || multiply == 0) return "0.00" // ← 明確處理 0

        val result = this * multiply
        val decimal = BigDecimal(result).divide(BigDecimal(10000))
        return decimal.setScale(2, RoundingMode.DOWN).toPlainString()
    }

    fun Long.percent(p: Int): Long {
        return this * p / 100
    }
}