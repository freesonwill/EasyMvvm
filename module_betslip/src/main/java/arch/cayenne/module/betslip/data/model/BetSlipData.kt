package arch.cayenne.module.betslip.data.model

import arch.cayenne.module.betslip.data.constants.BetSlipExpandedEnum
import galaxy.common.proto.Common

sealed class BetSlipData {
    abstract var expandedEnum: BetSlipExpandedEnum
}

data class BetSlipOrder(
    val order: Common.Order,
    override var expandedEnum: BetSlipExpandedEnum = BetSlipExpandedEnum.Hide
) : BetSlipData()

data class BetSlipReserve(
    val reserve: Common.ReserveOrder,
    override var expandedEnum: BetSlipExpandedEnum = BetSlipExpandedEnum.Hide
) : BetSlipData()