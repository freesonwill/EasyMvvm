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

    fun String.multiplication(multiplier: String): String {
        if (isEmpty() || this == "0" || multiplier.isEmpty() || multiplier == "0") return "0"
        if (multiplier == "1") return this
        if (this == "1") return multiplier

        // 處理負數
        val thisIsNegative = startsWith("-")
        val multiplierIsNegative = multiplier.startsWith("-")
        val resultIsNegative = thisIsNegative != multiplierIsNegative

        val thisAbs = if (thisIsNegative) drop(1) else this
        val multiplierAbs = if (multiplierIsNegative) multiplier.drop(1) else multiplier

        // 計算絕對值的乘積
        val result = multiplyAbsoluteValues(thisAbs, multiplierAbs)

        // 限制結果到小數點第二位
        val limitedResult = limitDecimalPlaces(result, 2)

        return if (resultIsNegative && limitedResult != "0") "-$limitedResult" else limitedResult
    }

    private fun limitDecimalPlaces(number: String, maxDecimalPlaces: Int): String {
        if (!number.contains(".")) return number

        val parts = number.split(".")
        val intPart = parts[0]
        val decPart = parts[1]

        // 截取小數部分，最多保留指定位數
        val limitedDecPart = if (decPart.length > maxDecimalPlaces) {
            decPart.substring(0, maxDecimalPlaces)
        } else {
            decPart
        }

        // 移除尾隨零
        val trimmedDecPart = limitedDecPart.trimEnd('0')

        return if (trimmedDecPart.isEmpty()) {
            intPart
        } else {
            "$intPart.$trimmedDecPart"
        }
    }


    private fun multiplyAbsoluteValues(num1: String, num2: String): String {
        // 分離整數和小數部分
        val parts1 = num1.split(".")
        val int1 = parts1[0]
        val dec1 = if (parts1.size > 1) parts1[1] else ""

        val parts2 = num2.split(".")
        val int2 = parts2[0]
        val dec2 = if (parts2.size > 1) parts2[1] else ""

        // 計算總小數位數
        val totalDecimalPlaces = dec1.length + dec2.length

        // 將兩個數字都當作整數處理
        val fullNum1 = int1 + dec1
        val fullNum2 = int2 + dec2

        // 執行乘法運算
        val result = multiplyStringNumbers(fullNum1, fullNum2)

        // 重新插入小數點並格式化
        val formattedResult = if (totalDecimalPlaces > 0) {
            if (result.length <= totalDecimalPlaces) {
                "0." + "0".repeat(totalDecimalPlaces - result.length) + result
            } else {
                val intPart = result.substring(0, result.length - totalDecimalPlaces)
                val decPart = result.substring(result.length - totalDecimalPlaces)
                "$intPart.$decPart"
            }
        } else {
            result
        }

        return formatDecimalString(formattedResult)
    }

    private fun multiplyStringNumbers(num1: String, num2: String): String {
        val digits1 = num1.reversed().map { it.digitToInt() }
        val digits2 = num2.reversed().map { it.digitToInt() }

        // 結果數組，最大長度為兩數長度之和
        val result = IntArray(digits1.size + digits2.size) { 0 }

        // 逐位相乘
        for (i in digits1.indices) {
            for (j in digits2.indices) {
                val product = digits1[i] * digits2[j]
                val pos = i + j

                result[pos] += product

                // 處理進位
                if (result[pos] >= 10) {
                    result[pos + 1] += result[pos] / 10
                    result[pos] %= 10
                }
            }
        }

        // 轉換為字串並移除前導零
        return result.reversed()
            .joinToString("")
            .trimStart('0')
            .ifEmpty { "0" }
    }

    fun String.multiplication(multiplier: Int): String {
        if (isEmpty() || this == "0" || multiplier == 0) return ""
        if (multiplier == 1) return this

        return multiplication("$multiplier")
    }

    fun List<String>.sumOf(): String {
        if (isEmpty()) return "0"

        val result = fold("0") { acc, number ->
            addTwoStrings(acc, number)
        }

        // 限制結果到小數點第二位
        return limitDecimalPlaces(result, 2)
    }

    private fun addTwoStrings(num1: String, num2: String): String {
        if (num1.isEmpty() || num1 == "0") return num2
        if (num2.isEmpty() || num2 == "0") return num1

        // 分離整數和小數部分
        val parts1 = num1.split(".")
        val int1 = parts1[0]
        val dec1 = if (parts1.size > 1) parts1[1] else ""

        val parts2 = num2.split(".")
        val int2 = parts2[0]
        val dec2 = if (parts2.size > 1) parts2[1] else ""

        // 統一小數位數
        val maxDecimalPlaces = maxOf(dec1.length, dec2.length)
        val paddedDec1 = dec1.padEnd(maxDecimalPlaces, '0')
        val paddedDec2 = dec2.padEnd(maxDecimalPlaces, '0')

        // 將兩個數字都當作整數處理
        val fullNum1 = int1 + paddedDec1
        val fullNum2 = int2 + paddedDec2

        // 執行加法運算
        val result = addStringNumbers(fullNum1, fullNum2)

        // 重新插入小數點並格式化
        val formattedResult = if (maxDecimalPlaces > 0) {
            if (result.length <= maxDecimalPlaces) {
                "0." + "0".repeat(maxDecimalPlaces - result.length) + result
            } else {
                val intPart = result.substring(0, result.length - maxDecimalPlaces)
                val decPart = result.substring(result.length - maxDecimalPlaces)
                "$intPart.$decPart"
            }
        } else {
            result
        }

        return formatDecimalString(formattedResult)
    }

    private fun addStringNumbers(num1: String, num2: String): String {
        val digits1 = num1.reversed().map { it.digitToInt() }
        val digits2 = num2.reversed().map { it.digitToInt() }
        val maxLength = maxOf(digits1.size, digits2.size)

        val result = mutableListOf<Int>()
        var carry = 0

        for (i in 0 until maxLength) {
            val digit1 = if (i < digits1.size) digits1[i] else 0
            val digit2 = if (i < digits2.size) digits2[i] else 0

            val sum = digit1 + digit2 + carry
            result.add(sum % 10)
            carry = sum / 10
        }

        if (carry > 0) {
            result.add(carry)
        }

        return result.reversed().joinToString("").trimStart('0').ifEmpty { "0" }
    }

    private fun formatDecimalString(number: String): String {
        if (!number.contains(".")) return number

        val trimmed = number.trimEnd('0')
        return if (trimmed.endsWith(".")) {
            trimmed.dropLast(1)
        } else {
            trimmed
        }
    }

    fun String.isGreaterThanZero(): Boolean {
        if (isEmpty() || startsWith("-")) return false

        // 移除所有的 0 和小數點，看是否還有其他數字
        val digitsOnly = replace("0", "").replace(".", "")
        return digitsOnly.isNotEmpty() && digitsOnly.all { it.isDigit() }
    }

    fun String.plusNumber(other: String): String {
        return addTwoStrings(this, other)
    }

    fun String.minusNumber(other: String): String {
        if (other.isEmpty() || other == "0") return this
        if (isEmpty() || this == "0") return if (other.startsWith("-")) other.drop(1) else "-$other"

        // 處理負數情況
        val thisIsNegative = startsWith("-")
        val otherIsNegative = other.startsWith("-")

        val thisAbs = if (thisIsNegative) drop(1) else this
        val otherAbs = if (otherIsNegative) other.drop(1) else other

        return when {
            !thisIsNegative && !otherIsNegative -> {
                // 正數 - 正數
                if (compareAbsoluteValues(thisAbs, otherAbs) >= 0) {
                    subtractAbsoluteValues(thisAbs, otherAbs)
                } else {
                    "-" + subtractAbsoluteValues(otherAbs, thisAbs)
                }
            }
            thisIsNegative && !otherIsNegative -> {
                // 負數 - 正數 = -(|this| + |other|)
                "-" + addAbsoluteValues(thisAbs, otherAbs)
            }
            !thisIsNegative && otherIsNegative -> {
                // 正數 - 負數 = 正數 + 正數
                addAbsoluteValues(thisAbs, otherAbs)
            }
            else -> {
                // 負數 - 負數 = -|this| + |other| = |other| - |this|
                if (compareAbsoluteValues(otherAbs, thisAbs) >= 0) {
                    subtractAbsoluteValues(otherAbs, thisAbs)
                } else {
                    "-" + subtractAbsoluteValues(thisAbs, otherAbs)
                }
            }
        }
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

    private fun subtractAbsoluteValues(larger: String, smaller: String): String {
        // 統一小數位數
        val parts1 = larger.split(".")
        val parts2 = smaller.split(".")

        val int1 = parts1[0]
        val dec1 = if (parts1.size > 1) parts1[1] else ""
        val int2 = parts2[0]
        val dec2 = if (parts2.size > 1) parts2[1] else ""

        val maxDecimalPlaces = maxOf(dec1.length, dec2.length)
        val paddedDec1 = dec1.padEnd(maxDecimalPlaces, '0')
        val paddedDec2 = dec2.padEnd(maxDecimalPlaces, '0')

        // 將兩個數字都當作整數處理
        val fullNum1 = int1 + paddedDec1
        val fullNum2 = int2 + paddedDec2

        // 執行減法運算
        val result = subtractStringNumbers(fullNum1, fullNum2)

        // 重新插入小數點並格式化
        val formattedResult = if (maxDecimalPlaces > 0) {
            if (result.length <= maxDecimalPlaces) {
                "0." + "0".repeat(maxDecimalPlaces - result.length) + result
            } else {
                val intPart = result.substring(0, result.length - maxDecimalPlaces)
                val decPart = result.substring(result.length - maxDecimalPlaces)
                "$intPart.$decPart"
            }
        } else {
            result
        }

        return formatDecimalString(formattedResult)
    }

    private fun subtractStringNumbers(num1: String, num2: String): String {
        val digits1 = num1.reversed().map { it.digitToInt() }.toMutableList()
        val digits2 = num2.reversed().map { it.digitToInt() }

        // 確保 digits1 長度足夠
        while (digits1.size < digits2.size) {
            digits1.add(0)
        }

        var borrow = 0
        for (i in digits2.indices) {
            var diff = digits1[i] - digits2[i] - borrow
            if (diff < 0) {
                diff += 10
                borrow = 1
            } else {
                borrow = 0
            }
            digits1[i] = diff
        }

        // 處理剩餘的借位
        var i = digits2.size
        while (borrow > 0 && i < digits1.size) {
            var diff = digits1[i] - borrow
            if (diff < 0) {
                diff += 10
                borrow = 1
            } else {
                borrow = 0
            }
            digits1[i] = diff
            i++
        }

        return digits1.reversed().joinToString("").trimStart('0').ifEmpty { "0" }
    }

    private fun addAbsoluteValues(num1: String, num2: String): String {
        // 重用之前的加法邏輯
        return addTwoStrings(num1, num2)
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