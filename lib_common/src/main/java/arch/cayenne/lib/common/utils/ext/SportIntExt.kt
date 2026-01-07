package arch.cayenne.lib.common.utils.ext

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale
import arch.cayenne.lib.common.utils.ext.SportDisplayOddsExt.getDisplayOdds
object SportIntExt {
    /**
     * @return string: 1234 轉換為 12.34, 1000 轉換為 10
     */
    fun Long.getMoney(stripTrailingZero:Boolean=true): String {
        if (this == 0L) return "0" // ← 明確處理 0

        val value = this.toBigDecimal().divide(BigDecimal(100))
            .setScale(2, RoundingMode.DOWN)
        if(!stripTrailingZero) return value.toPlainString()

        return if (value.stripTrailingZeros().scale() <= 0) {
            value.toPlainString().split(".")[0] // 顯示整數
        } else {
            value.stripTrailingZeros().toPlainString() // 去除多餘 0
        }
    }

    fun Long.getMoneyForScale(scale: Int = 2): String {
        if (this == 0L) {
            // 根據 scale 回傳格式化的零值字串
            return "0." + "0".repeat(scale)
        }

        val divisor = BigDecimal.TEN.pow(scale)
        val value = this.toBigDecimal().divide(divisor, scale, RoundingMode.DOWN)
            .stripTrailingZeros() // 去除尾部多餘的 0

        // 當結果是整數時，直接用 toPlainString() 會是整數表示，這裡如果想強制有小數點可特別處理
        val resultStr = value.toPlainString()

        // 如果結果是小數但末尾沒有小數點，補足小數點（視需求決定要不要）
        return if (resultStr.contains(".")) {
            resultStr
        } else {
            resultStr // 例如 "1" 就回傳 "1"，不強制補小數點
        }
    }

    /***
     * @param odds 乘數: 通常為賠率
     */
    fun Long.getMoney(odds: Int,stripTrailingZero:Boolean=true): String {
        return getMoney(odds.toLong(),stripTrailingZero)
    }

    /***
     * @param odds 乘數: 通常為賠率
     */
    fun Long.getMoney(odds: Long,stripTrailingZero:Boolean=true): String {
        if (this == 0L || odds <= 0) return "0" // ← 明確處理 0
        val decimal = BigDecimal(this).multiply(BigDecimal(odds))
            .divide(BigDecimal(10000))
            .setScale(2, RoundingMode.DOWN)
        if(!stripTrailingZero) return decimal.toPlainString()

        return if (decimal.stripTrailingZeros().scale() <= 0) {
            decimal.toPlainString().split(".")[0] // 僅整數部分
        } else {
            decimal.stripTrailingZeros().toPlainString()
        }
    }

    /**
     * @return string: 123456 轉換為 1,234.56, 123456789 轉換為 1,234,567.89
     */
    fun Long.getFormalMoney(stripTrailingZero:Boolean=true): String {
        if (this == 0L) return "0"

        val value = this.toBigDecimal().divide(BigDecimal(100)).setScale(2, RoundingMode.DOWN)
        if(!stripTrailingZero) return value.toPlainString()
        val stripped = value.stripTrailingZeros()

        val numberFormat = NumberFormat.getNumberInstance(Locale.US).apply {
            maximumFractionDigits = 2
            minimumFractionDigits = 0
            isGroupingUsed = true // 千分位
        }

        return numberFormat.format(stripped)
    }

    fun Long.getFormalMoney(odds: Int): String {
        if (this == 0L || odds <= 0) return "0" // ← 明確處理 0

        val result = this * odds
        val decimal = BigDecimal(result).divide(BigDecimal(10000))
            .setScale(2, RoundingMode.DOWN)

        val stripped = decimal.stripTrailingZeros()

        val numberFormat = NumberFormat.getNumberInstance(Locale.US).apply {
            maximumFractionDigits = 2
            minimumFractionDigits = 0
            isGroupingUsed = true // 千分位
        }

        return numberFormat.format(stripped)
    }

    fun Long.getFormalMoney(money: Long, scale: Int = 2): String {
        if (this == 0L || money == 0L) return "0" // ← 明確處理 0

        val result = this * money

        // 動態計算分母：100 * 10^scale
        val divisor = BigDecimal(100).multiply(BigDecimal.TEN.pow(scale))

        val decimal = BigDecimal(result)
            .divide(divisor, 2, RoundingMode.DOWN) // 最終顯示仍保留 2 位小數

        val stripped = decimal.stripTrailingZeros()

        val numberFormat = NumberFormat.getNumberInstance(Locale.US).apply {
            maximumFractionDigits = 2
            minimumFractionDigits = 0
            isGroupingUsed = true
        }

        return numberFormat.format(stripped)
    }

    /**
     * 欧洲盘赔率（含本金）
     * @see [getDisplayOdds]
     * @return string: 1234 轉換為 12.34, 1000 轉換為 10.00
     */
    fun Int.getOdds(stripTrailingZero:Boolean = true): String {
        return this.toLong().getOdds(stripTrailingZero)
    }

    fun Long.getOdds(stripTrailingZero:Boolean = true): String {
        if (this <= 0) return DecimalFormat("#.##").format(this/100f)
        //BigDecimal(this / 100f).setScale(2, RoundingMode.HALF_UP).toFloat()
        //(this / 100f).let { String.format("%.2f", it).toFloat() }

        val decimal = BigDecimal(this).divide(BigDecimal(100))
        return decimal.setScale(2, RoundingMode.DOWN)
            .apply { if(stripTrailingZero) stripTrailingZeros() }
            .toPlainString()
    }

    fun Int.getOdds(odds: Int,stripTrailingZero:Boolean = true): String {
        if (this <= 0) return DecimalFormat("#.##").format(this/100f)// ← 明確處理 0

        val decimal = BigDecimal(this).multiply(BigDecimal(odds)).divide(BigDecimal(10000))
        return decimal.setScale(2, RoundingMode.DOWN)
            .apply { if(stripTrailingZero) stripTrailingZeros() }
            .toPlainString()
    }

    fun Long.percent(p: Int): Long {
        return this * p / 100
    }

    fun Double.getFormalMoney(): String {
        val bd = BigDecimal.valueOf(this).stripTrailingZeros()
        return if (bd.scale() <= 0) {
            // 整數 or 0 → 固定兩位
            DecimalFormat("0.00").format(this)
        } else {
            // 有小數 → 最多 8 位，不補 0
            bd.setScale(minOf(bd.scale(), 8), RoundingMode.DOWN)
                .toPlainString()
        }
    }

    fun Long.toBalanceString(scale: Int = 2): String {
        val MAX_LENGTH = 10
        val MAX_DECIMAL = 8
        val divisor = BigDecimal.TEN.pow(scale)
        val bd = BigDecimal(this).divide(divisor).stripTrailingZeros()
        val plain = bd.toPlainString()

        // ===== 規則 2.2：0 或 純整數 =====
        if (this == 0L || !plain.contains(".")) {
            return "${bd.setScale(2, RoundingMode.DOWN).toPlainString()}"
        }

        val parts = plain.split(".")
        val integerPart = parts[0]
        val decimalPart = parts.getOrElse(1) { "" }

        // ===== 條件 1：超過字元限制 =====
        if (plain.length > MAX_LENGTH) {
            val availableDecimal =
                MAX_LENGTH - integerPart.length - 1 - 3 // . + ...

            if (availableDecimal <= 0) {
                return "$integerPart..."
            }

            val trimmedDecimal = decimalPart
                .take(MAX_DECIMAL)
                .take(availableDecimal)

            return "$integerPart.$trimmedDecimal..."
        }

        // ===== 條件 2：未超過字元限制 =====

        val decimalUpTo8 = decimalPart.take(MAX_DECIMAL)

        // 規則 2.1：小數點後 8 位內有非零
        val hasNonZero = decimalUpTo8.any { it != '0' }

        return when {
            // 小數點後完全沒有數字（理論上不會進來，保護用）
            decimalUpTo8.isEmpty() -> {
                "$integerPart.00"
            }

            // 小數點後 8 位內有非零 → 完整顯示並去尾 0
            hasNonZero -> {
                val cleanedDecimal = decimalUpTo8.trimEnd('0')
                "$integerPart.$cleanedDecimal"
            }

            // 小數點後全為 0 → 顯示 2 位
            else -> {
                "$integerPart.00"
            }
        }
    }
}