package arch.cayenne.module.betslip.data.model

import arch.cayenne.module.betslip.data.constants.BetSlipExpandedEnum

sealed class BetSlipData {
    abstract var expandedEnum: BetSlipExpandedEnum
}

data class BetSlipOrder(
    val order: OrderBean,
    override var expandedEnum: BetSlipExpandedEnum = BetSlipExpandedEnum.Hide
) : BetSlipData()

data class BetSlipReserve(
    val reserve: ReserveOrderBean,
    override var expandedEnum: BetSlipExpandedEnum = BetSlipExpandedEnum.Hide
) : BetSlipData()