package arch.cayenne.module.betslip.data.model

import arch.cayenne.module.betslip.data.constants.BetSlipExpandedEnum

sealed class BetSlipData {
    abstract var expandedEnum: BetSlipExpandedEnum
}