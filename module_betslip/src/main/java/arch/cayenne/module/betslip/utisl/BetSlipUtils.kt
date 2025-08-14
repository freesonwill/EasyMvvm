package arch.cayenne.module.betslip.utisl

import arch.cayenne.lib.common.utils.ext.SportIntExt.getFormalMoney
import arch.cayenne.lib.common.utils.ext.SportIntExt.getMoney
import arch.cayenne.lib.common.utils.ext.SportStringExt.toMoney
import arch.cayenne.lib.common.utils.ext.SportStringExt.toMoneyForScale

internal object BetSlipUtils {

    /**
     * 计算预计最高金额
     * */
    fun expectMaxAmount(betAmount: Long, odds: Int): String {
        val result = betAmount.getMoney(odds).toMoney().minus(betAmount)
        return result.getFormalMoney()
    }


    fun earlySettlePrice(betAmount: Long, earlyBetAmount: Long, price: String): String {
        val scale = getPriceScale(price)
        val mPrice = price.toMoneyForScale(scale)
        val result = betAmount.minus(earlyBetAmount)
        return result.getFormalMoney(mPrice, scale)
    }

    private fun getPriceScale(price: String): Int {
        // 尋找字串中的小數點索引
        val dotIndex = price.indexOf('.')

        // 如果沒有小數點，則回傳 0
        if (dotIndex == -1) {
            return 0
        }

        // 計算小數點後面的字元數量
        return price.length - dotIndex - 1
    }
}