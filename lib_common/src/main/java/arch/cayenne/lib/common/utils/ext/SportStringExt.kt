package arch.cayenne.lib.common.utils.ext

import java.math.BigDecimal
import java.math.RoundingMode

object SportStringExt {

    /**
     * @return 轉換後的整數值，若轉換失敗則返回 0, ex "1.23" -> 123, "0.5" -> 50
     */
    fun String.toMoney(): Long {
        if (this == "0L") return 0 // 明確處理 0

        val value = if (this.last() == '.') {
            this.substring(0, this.length - 1)
        } else {
            this
        }
        return try {
            val decimal = BigDecimal(value).setScale(2, RoundingMode.DOWN)
            decimal.multiply(BigDecimal(100)).toLong()
        } catch (e: NumberFormatException) {
            0 // 或依需求處理錯誤情況
        }
    }

    /**
     * @return 轉換後的整數值，若轉換失敗則返回 0, ex "1.23" -> 123, "0.5" -> 50
     */
    fun String.toOdds(): Int {
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