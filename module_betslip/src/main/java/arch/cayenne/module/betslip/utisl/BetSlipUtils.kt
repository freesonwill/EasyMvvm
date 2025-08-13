package arch.cayenne.module.betslip.utisl

import arch.cayenne.lib.common.utils.ext.SportIntExt.getFormalMoney
import arch.cayenne.lib.common.utils.ext.SportIntExt.getMoney
import arch.cayenne.lib.common.utils.ext.SportStringExt.toMoney

internal object BetSlipUtils {

    /**
     * 计算预计最高金额
     * */
    fun expectMaxAmount(betAmount: Long, odds: Int): String {
        val result = betAmount.getMoney(odds).toMoney().minus(betAmount)
        return result.getFormalMoney()
    }


    fun earlySettlePrice(betAmount: Long, earlyBetAmount: Long, price: Long): String {
        val result = betAmount.minus(earlyBetAmount)
        return result.getFormalMoney(price)
    }

}