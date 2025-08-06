package arch.cayenne.lib.common.utils.ext

import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import org.koin.java.KoinJavaComponent.inject
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale

/**
 * 負責最終顯示於畫面上的賠率與金額，根據歐洲盤或香港盤做變化
 */
object SportMoneyOddsExt {

    private val manager: UserDataManager = inject<UserDataManager>(UserDataManager::class.java).value
    private val oddsType: Int get() = manager.getValue(UserDataKey.KEY_ODDS, 0)

    fun Long.getDisplayFormalMoney(odds: Int): String {
        if (this == 0L || odds <= 0) return "0"

        val result = this * (odds - oddsType * 100)
        val decimal = BigDecimal(result).divide(BigDecimal(10000))
            .setScale(2, RoundingMode.DOWN)

        val stripped = decimal.stripTrailingZeros()

        val numberFormat = NumberFormat.getNumberInstance(Locale.US).apply {
            maximumFractionDigits = 2
            minimumFractionDigits = 0
            isGroupingUsed = true
        }

        return numberFormat.format(stripped)
    }

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
}