package arch.cayenne.module.betslip.utisl

import arch.cayenne.module.betslip.data.model.BetSlipData

interface BetSlipAdapterMangerInterface {
    fun createViewHolder()

    fun covertPlus(item: BetSlipData)
}