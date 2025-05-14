package arch.cayenne.module.betslip.utisl

import galaxy.common.proto.Common.Order
import java.math.BigDecimal
import java.math.RoundingMode

object LiveBetSlipUtils {
    /**
     * 计算预计最高金额
     * */
    fun expectMaxAmount(betAmount: String, odds: String): String {
        return toBigDecimal(betAmount).multiply(toBigDecimal(odds))
            .setScale(2, RoundingMode.HALF_UP).toString()
    }

    /**
     * 计算输赢金额
     * */
    fun winOrLoseAmount(order: Order): String {
        return toBigDecimal(order.betAmount).minus(toBigDecimal(order.earlyBetAmount)).minus(
            toBigDecimal(order.returnAmount).setScale(2, RoundingMode.HALF_UP)
        ).toString()
    }

    /***
     * 提前结算金额
     * */
    fun earlySettlePrice(betAmount: String, earlyPrice: String, earlyBetAmount: String): String {
        return toBigDecimal(betAmount).multiply(toBigDecimal(earlyPrice))
            .minus(toBigDecimal(earlyBetAmount)).setScale(2, RoundingMode.HALF_UP).toString()
    }

    fun toBigDecimal(value: String?): BigDecimal {
        return value?.toBigDecimalOrNull() ?: BigDecimal(0)
    }

}