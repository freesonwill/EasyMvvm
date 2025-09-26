package arch.cayenne.lib.common.utils.ext

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

    fun String.isGreaterThanZero(): Boolean {
        if (isEmpty() || startsWith("-")) return false

        // 移除所有的 0 和小數點，看是否還有其他數字
        val digitsOnly = replace("0", "").replace(".", "")
        return digitsOnly.isNotEmpty() && digitsOnly.all { it.isDigit() }
    }

    private fun compareAbsoluteValues(num1: String, num2: String): Int {
        // 標準化數字（移除前導零）
        val normalized1 = normalizeForComparison(num1)
        val normalized2 = normalizeForComparison(num2)

        val parts1 = normalized1.split(".")
        val parts2 = normalized2.split(".")

        val int1 = parts1[0]
        val int2 = parts2[0]

        // 比較整數部分長度
        if (int1.length != int2.length) {
            return int1.length.compareTo(int2.length)
        }

        // 長度相同，逐位比較整數部分
        val intComparison = int1.compareTo(int2)
        if (intComparison != 0) return intComparison

        // 整數部分相同，比較小數部分
        val dec1 = if (parts1.size > 1) parts1[1] else ""
        val dec2 = if (parts2.size > 1) parts2[1] else ""

        val maxDecLength = maxOf(dec1.length, dec2.length)
        val paddedDec1 = dec1.padEnd(maxDecLength, '0')
        val paddedDec2 = dec2.padEnd(maxDecLength, '0')

        return paddedDec1.compareTo(paddedDec2)
    }
    private fun normalizeForComparison(number: String): String {
        val parts = number.split(".")
        val intPart = parts[0].trimStart('0').ifEmpty { "0" }
        val decPart = if (parts.size > 1) parts[1].trimEnd('0') else ""

        return if (decPart.isEmpty()) intPart else "$intPart.$decPart"
    }


    fun String.isGreaterThanValue(value: String): Boolean {
        if (isEmpty() && value.isEmpty()) return false
        if (isEmpty()) return false
        if (value.isEmpty()) return isGreaterThanZero()

        val thisIsNegative = startsWith("-")
        val valueIsNegative = value.startsWith("-")

        return when {
            !thisIsNegative && valueIsNegative -> true  // 正數 > 負數
            thisIsNegative && !valueIsNegative -> false // 負數 < 正數
            !thisIsNegative && !valueIsNegative -> {
                // 兩個都是正數，比較絕對值
                compareAbsoluteValues(this, value) > 0
            }
            else -> {
                // 兩個都是負數，絕對值小的反而大
                val thisAbs = drop(1)
                val valueAbs = value.drop(1)
                compareAbsoluteValues(thisAbs, valueAbs) < 0
            }
        }
    }

}