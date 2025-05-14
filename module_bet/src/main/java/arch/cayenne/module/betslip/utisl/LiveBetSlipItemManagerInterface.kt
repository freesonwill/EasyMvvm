package arch.cayenne.module.betslip.utisl

import arch.cayenne.module.betslip.data.constants.LiveBetSlipExpandedEnum
import arch.cayenne.module.betslip.data.model.LiveBetSlipSelectionData


interface LiveBetSlipItemManagerInterface {
    fun createViewHolder()

    fun covertPlus(
        position: Int,
        count: Int,
        expandedEnum: LiveBetSlipExpandedEnum,
        item: LiveBetSlipSelectionData
    )
}