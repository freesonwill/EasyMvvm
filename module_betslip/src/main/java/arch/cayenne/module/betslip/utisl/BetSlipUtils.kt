package arch.cayenne.module.betslip.utisl

import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.utils.ext.SportIntExt.getFormalMoney
import arch.cayenne.lib.common.utils.ext.SportIntExt.getMoney
import arch.cayenne.lib.common.utils.ext.SportStringExt.toMoney
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale

internal object BetSlipUtils {
    /**
     * 计算预计最高金额
     * */
    fun expectMaxAmount(betAmount: String, odds: String): String {
        val result = multipy1000(betAmount).multiply(multipy1000(odds))
            .divide(BigDecimal(1000000)).minus(BigDecimal(betAmount)).setScale(2, RoundingMode.DOWN)
        return result.getFormalMoney()
    }

    /**
     * 计算输赢金额
     * */
    fun winOrLoseAmount(betAmount: String, earlyBetAmount: String, returnAmount: String): Double {
        return multipy1000(betAmount).minus(multipy1000(earlyBetAmount)).minus(
            multipy1000(returnAmount).divide(BigDecimal(1000)).setScale(2, RoundingMode.DOWN)
        ).toDouble()
    }

    /***
     * 提前结算金额
     * */
    fun earlySettlePrice(betAmount: String, earlyBetAmount: String): String {
        val nBetAmount = betAmount.toMoney()
        val nEarlyBetAmount = earlyBetAmount.toMoney()
        val result = nBetAmount.minus(nEarlyBetAmount)
        return BigDecimal(result.getFormalMoney()).setScale(2, RoundingMode.DOWN).toPlainString()
    }

    /***
     * 计算最小结算金额
     * */
    fun calculateMinSettlePrice(betAmount: String, earlyBetAmount: String, minSettleAmount: String): Boolean {
        val nBetAmount = betAmount.toMoney()
        val nEarlyBetAmount = earlyBetAmount.toMoney()
        val nMinSettleAmount = minSettleAmount.toMoney()
        val result = nBetAmount.minus(nEarlyBetAmount)
        return result >= nMinSettleAmount
    }

    private fun toBigDecimal(value: String?): BigDecimal {
        return value?.toBigDecimalOrNull() ?: BigDecimal(0)
    }

    private fun multipy1000(value: String): BigDecimal {
        return toBigDecimal(value).multiply(BigDecimal(1000))
    }

}