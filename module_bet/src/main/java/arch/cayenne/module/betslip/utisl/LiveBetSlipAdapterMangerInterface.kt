package arch.cayenne.module.betslip.utisl

interface LiveBetSlipAdapterMangerInterface {
    fun createViewHolder()

    fun covertPlus(position: Int, item: arch.cayenne.module.betslip.data.model.LiveBetSlipData)
}