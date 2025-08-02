package arch.cayenne.module.betslip.utisl

import arch.cayenne.lib.common.utils.ext.SportIntExt.getFormalMoney
import arch.cayenne.lib.common.utils.ext.SportIntExt.getMoney
import arch.cayenne.lib.common.utils.ext.SportStringExt.toMoney
import arch.cayenne.lib.common.utils.ext.SportStringExt.toOdds

internal object BetSlipUtils {

    /**
     * 计算预计最高金额
     * */
    fun expectMaxAmount(betAmount: String, odds: String): String {
        val nBetAmount = betAmount.toMoney()
        val nOdds = odds.toOdds()
        val result = nBetAmount.getMoney(nOdds).toMoney().minus(nBetAmount)
        return result.getFormalMoney()
    }


    fun earlySettlePrice(betAmount: String, earlyBetAmount: String, price: String): String {
        val nBetAmount = betAmount.toMoney()
        val nEarlyBetAmount = earlyBetAmount.toMoney()
        val nPrice = price.toMoney()
        val result = nBetAmount.minus(nEarlyBetAmount)
        return result.getFormalMoney(nPrice)
    }

}