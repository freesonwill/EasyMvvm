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

    fun String.getHomeScore(): String {
        return if (this.contains(":")) this.substringBefore(":").trim() else ""
    }

    fun String.getAwayScore(): String {
        return if (this.contains(":")) this.substringAfter(":").trim() else ""
    }

    fun String.limitTitleLength(maxUnits: Int = 5): String {
        var units = 0.0
        val builder = StringBuilder()
        for (char in this) {
            val unit = if (char.code in 0..127) 0.5 else 1.0
            if (units + unit > maxUnits) break
            builder.append(char)
            units += unit
        }
        return if (builder.length < this.length) builder.toString() + "…" else builder.toString()
    }

    /**
     * @return 轉換後的整數值，若轉換失敗則返回 0, ex "123" -> 123L -> 實際上餘額為1.23元, "1.0E7" -> 10000000L -> 實際上餘額為100000元
     */
    fun String.balanceStringToLong(): Long {
        if (this == "0L") return 0 // 明確處理 0
        return try {
            BigDecimal(this)
                .setScale(2, RoundingMode.DOWN)
                .toLong()
        } catch (e: NumberFormatException) {
            0 // 或依需求處理錯誤情況
        }
    }


}