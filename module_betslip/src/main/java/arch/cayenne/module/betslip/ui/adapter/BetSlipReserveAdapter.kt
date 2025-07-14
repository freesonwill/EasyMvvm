package arch.cayenne.module.betslip.ui.adapter

import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.common.ui.adapter.RecyclerItemListener
import arch.cayenne.lib.database.entity.BetSlipReserveBean
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.ui.viewholder.BaseBetSlipViewHolder
import arch.cayenne.module.betslip.ui.viewholder.BetSlipReserveViewHolder

class BetSlipReserveAdapter(
    private val cancelReserveListener: RecyclerItemListener<BetSlipReserveBean>,
    private val modifyReserveListener: BetSlipReserveListener
) : BetSlipAdapter(BetSlipEnum.Reserve) {

    override fun createViewHolder(binding: ViewBinding, viewType: Int): BaseBetSlipViewHolder<*> {
        val holder = super.createViewHolder(binding, viewType)
        if (holder is BetSlipReserveViewHolder) {
            holder.setReserveModifySubmitListener(modifyReserveListener)
            holder.setCancelReserveSubmitListener(object : RecyclerItemListener<String> {
                override fun onItemClick(item: String?, position: Int) {
                    cancelReserveListener.onItemClick(getItem(position) as BetSlipReserveBean, position)
                }
            })
        }
        return holder
    }

    interface BetSlipReserveListener {
        fun onModifyReserveClick(locationX: Int, locationY: Int, viewHeight: Int, position: Int)
    }
}