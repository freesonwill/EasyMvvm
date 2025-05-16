package arch.cayenne.module.betslip.utisl

interface BetSlipAdapterMangerInterface {
    fun createViewHolder()

    fun covertPlus(position: Int, item: arch.cayenne.module.betslip.data.model.BetSlipData)
}