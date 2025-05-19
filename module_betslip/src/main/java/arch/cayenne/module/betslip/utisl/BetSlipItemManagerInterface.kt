package arch.cayenne.module.betslip.utisl

import arch.cayenne.module.betslip.data.constants.BetSlipExpandedEnum
import arch.cayenne.module.betslip.data.model.BetSlipSelectionData


interface BetSlipItemManagerInterface {
    fun createViewHolder()

    fun covertPlus(
        position: Int,
        count: Int,
        expandedEnum: BetSlipExpandedEnum,
        item: BetSlipSelectionData
    )
}