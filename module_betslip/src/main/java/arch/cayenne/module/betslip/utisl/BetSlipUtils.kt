package arch.cayenne.module.betslip.utisl

import arch.cayenne.lib.common.utils.ext.SportIntExt.getMoney
import arch.cayenne.lib.common.utils.ext.SportStringExt.toMoney
import arch.cayenne.module.betslip.data.constants.BetSlipExpandedEnum
import arch.cayenne.module.betslip.data.model.BetSlipData
import galaxy.common.proto.Common.Order
import java.math.BigDecimal
import java.math.RoundingMode

internal object BetSlipUtils {
    /**
     * 计算预计最高金额
     * */
    fun expectMaxAmount(betAmount: String, odds: String): String {
        return multipy1000(betAmount).multiply(multipy1000(odds))
            .divide(BigDecimal(1000000)).setScale(2, RoundingMode.DOWN).toString()
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
        return BigDecimal(result.getMoney()).setScale(2, RoundingMode.DOWN).toPlainString()
    }

    private fun toBigDecimal(value: String?): BigDecimal {
        return value?.toBigDecimalOrNull() ?: BigDecimal(0)
    }

    private fun multipy1000(value: String): BigDecimal {
        return toBigDecimal(value).multiply(BigDecimal(1000))
    }

    fun List<Order>.toBetSlipData(): List<BetSlipData> {
        return this.map {
            val expandedEnum =
                if (it.selectionsList.size <= 3) BetSlipExpandedEnum.Hide else BetSlipExpandedEnum.Fold
            BetSlipData(
                order = it, expandedEnum = expandedEnum
            )
        }.toList()
    }


}