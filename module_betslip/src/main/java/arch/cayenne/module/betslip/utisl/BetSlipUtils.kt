package arch.cayenne.module.betslip.utisl

import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.module.betslip.data.constants.BetSlipExpandedEnum
import arch.cayenne.module.betslip.data.model.BetSlipData
import galaxy.common.proto.Common
import galaxy.common.proto.Common.Order
import java.math.BigDecimal
import java.math.RoundingMode

object BetSlipUtils {
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
    fun winOrLoseAmount(betAmount: String, earlyBetAmount: String, returnAmount: String): Double {
        return toBigDecimal(betAmount).minus(toBigDecimal(earlyBetAmount)).minus(
            toBigDecimal(returnAmount).setScale(2, RoundingMode.HALF_UP)
        ).toDouble()
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

    internal fun List<Common.Order>.toBetSlipData(): List<BetSlipData> {
        return this.map {
            val expandedEnum =
                if (it.selectionsList.size <= 3) BetSlipExpandedEnum.Hide else BetSlipExpandedEnum.Fold
            BetSlipData(
                order = it, expandedEnum = expandedEnum
            )
        }.toList()
    }
}