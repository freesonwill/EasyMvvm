package arch.cayenne.module.betslip.utisl

import arch.cayenne.lib.database.entity.BetSlipSelectionData


interface BetSlipItemViewHolderInterface {

    fun covertPlus(
        item: BetSlipSelectionData
    )
}