package arch.cayenne.lib.common.utils.ext

import arch.cayenne.lib.common.utils.ext.SportStringExt.toOdds
import java.math.BigDecimal
import java.math.RoundingMode

object SportStringExt {

    /**
     * @return 轉換後的整數值，若轉換失敗則返回 0, ex "1.23" -> 123, "0.5" -> 50
     */
    fun String.toMoney(): Long {
        if (this == "0L" || this.isEmpty()) return 0 // 明確處理 0

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
     * @param scale 小數轉換倍數：1 -> *10, 2 -> *100, 3 -> *1000
     * @return 轉換後的整數值，若轉換失敗則返回 0
     * 範例：
     *  scale=1 -> "1.23" → 12, "0.5" → 5
     *  scale=2 -> "1.23" → 123, "0.5" → 50
     *  scale=3 -> "1.23" → 1230, "0.5" → 500
     */
    fun String.toMoneyForScale(scale: Int = 2): Long {
        if (this == "0L" || this.isEmpty()) return 0

        val cleanValue = if (this.last() == '.') {
            this.dropLast(1)
        } else {
            this
        }

        return try {
            val decimal = BigDecimal(cleanValue).setScale(scale, RoundingMode.DOWN)
            val multiplier = BigDecimal.TEN.pow(scale)
            decimal.multiply(multiplier).toLong()
        } catch (e: NumberFormatException) {
            0
        }
    }

    /**
     * @return 轉換後的整數值，若轉換失敗則返回 0, ex "1.23" -> 123, "0.5" -> 50
     */
    fun String.toOdds(): Int {
        if (this == "0" || this.isEmpty()) return 0 // 明確處理 0
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
        if (this == "0L" || this.isEmpty()) return 0 // 明確處理 0
        return try {
            BigDecimal(this)
                .setScale(2, RoundingMode.DOWN)
                .multiply(BigDecimal(100)) //服务器给的余额*/100,,为了保证精确到分*100，显示的时候*/100
                .toLong()
        } catch (e: NumberFormatException) {
            0 // 或依需求處理錯誤情況
        }
    }

    fun String.timeStringToInt(): Int {
        if (this == "0" || this.isEmpty()) return 0
        return try {
            this.toInt()
        } catch (e: NumberFormatException) {
            0
        }
    }
}