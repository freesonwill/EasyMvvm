package com.walisport.module.live.utils

import galaxy.common.proto.Common.Order
import galaxy.common.proto.Common.ReserveOrder
import java.math.BigDecimal

object LiveBetSlipUtils {
    fun expectMaxAmount(betAmount:String,odds:String): String {
        return toBigDecimal(betAmount).multiply(toBigDecimal(odds)).toString()
    }

    fun winOrLoseAmount(order: Order): String {
        return toBigDecimal(order.betAmount).minus(toBigDecimal(order.earlyBetAmount)).minus(
            toBigDecimal(order.returnAmount)
        ).toString()
    }

    fun earlySettlePrice(betAmount: String,odds:String,earlyBetAmount:String): String {
        return toBigDecimal(betAmount).multiply(toBigDecimal(odds))
            .minus(toBigDecimal(earlyBetAmount)).toString()
    }

    private fun toBigDecimal(value: String): BigDecimal {
        return value.toBigDecimalOrNull() ?: BigDecimal(0)
    }

}