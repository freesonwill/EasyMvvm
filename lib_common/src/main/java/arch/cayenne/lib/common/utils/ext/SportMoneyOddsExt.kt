package arch.cayenne.lib.common.utils.ext

import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import org.koin.java.KoinJavaComponent.inject
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * 負責最終顯示於畫面上的賠率與金額，根據歐洲盤或香港盤做變化
 */
object SportMoneyOddsExt {

    private val manager: UserDataManager = inject<UserDataManager>(UserDataManager::class.java).value
    private val oddsType: Int get() = manager.getValue(UserDataKey.KEY_ODDS, 0)

    /**
     * @return string: 1234 轉換為 12.34, 1000 轉換為 10.00
     */
    fun Int.getDisplayOdds(): String {
        if (this <= 0) return "0"

        val newOdds = this - oddsType * 100
        if (newOdds <= 0) return "0"
        val decimal = BigDecimal(newOdds).divide(BigDecimal(100))
        return decimal.setScale(2, RoundingMode.DOWN).stripTrailingZeros().toPlainString()
    }

    fun Int.getDisplayOdds(odds: Int): String {
        if (this <= 0 || odds <= 0) return "0" // ← 明確處理 0

        val newOdds = this * odds - oddsType * 10000
        if (newOdds <= 0) return "0"
        val decimal = BigDecimal(newOdds).divide(BigDecimal(10000))
        return decimal.setScale(2, RoundingMode.DOWN).stripTrailingZeros().toPlainString()
    }

    fun Int.reserveDisplayOdds(): Int {
        if (this <= 0) return 0 // 明確處理 0
        val newOdds = this + oddsType * 100
        return if (newOdds <= 0) 0 else newOdds
    }

    fun String.reversDisplayOdds(): Int {
        if (this == "0" || this.isEmpty()) return 0 // 明確處理 0
        val value = if (this.last() == '.') {
            this.substring(0, this.length - 1)
        } else {
            this
        }
        return try {
            val decimal = BigDecimal(value).setScale(2, RoundingMode.DOWN)
            decimal.multiply(BigDecimal(100)).toInt() + oddsType * 100
        } catch (e: NumberFormatException) {
            0 // 或依需求處理錯誤情況
        }
    }
}