package arch.cayenne.module.betslip.utisl

import arch.cayenne.lib.database.entity.BetSlipData

interface BetSlipAdapterViewHolderInterface {
    fun createViewHolder()

    fun covertPlus(item: BetSlipData)
}