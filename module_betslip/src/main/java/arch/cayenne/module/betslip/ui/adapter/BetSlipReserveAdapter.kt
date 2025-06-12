package arch.cayenne.module.betslip.ui.adapter

import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.common.ui.adapter.RecyclerItemListener
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.data.model.BetSlipReserve
import arch.cayenne.module.betslip.ui.viewholder.BaseBetSlipViewHolder
import arch.cayenne.module.betslip.ui.viewholder.BetSlipReserveViewHolder

class BetSlipReserveAdapter : BetSlipAdapter(BetSlipEnum.Reserve) {

    private var cancelReserveListener: RecyclerItemListener<BetSlipReserve>? = null
    private var modifyReserveListener: RecyclerItemListener<BetSlipReserve>? = null

    override fun createViewHolder(binding: ViewBinding, viewType: Int): BaseBetSlipViewHolder<*> {
        val holder = super.createViewHolder(binding, viewType)
        if (holder is BetSlipReserveViewHolder) {
            holder.setReserveModifySubmitListener(object : RecyclerItemListener<String> {
                override fun onItemClick(item: String?, position: Int) {
                    modifyReserveListener?.onItemClick(getItem(position) as BetSlipReserve, position)
                }
            })
            holder.setCancelReserveSubmitListener(object : RecyclerItemListener<String> {
                override fun onItemClick(item: String?, position: Int) {
                    cancelReserveListener?.onItemClick(getItem(position) as BetSlipReserve, position)
                }
            })
        }
        return holder
    }

    fun setCancelReserveListener(listener: RecyclerItemListener<BetSlipReserve>) {
        this.cancelReserveListener = listener
    }

    fun setModifyReserveListener(listener: RecyclerItemListener<BetSlipReserve>) {
        this.modifyReserveListener = listener
    }
}