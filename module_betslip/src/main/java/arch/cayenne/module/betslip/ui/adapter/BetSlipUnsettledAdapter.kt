package arch.cayenne.module.betslip.ui.adapter

import android.view.ViewGroup
import arch.cayenne.lib.common.ui.adapter.RecyclerItemListener
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.data.model.BetSlipOrder
import arch.cayenne.module.betslip.ui.viewholder.BaseBetSlipViewHolder
import arch.cayenne.module.betslip.ui.viewholder.BetSlipUnsettledViewHolder

class BetSlipUnsettledAdapter : BetSlipAdapter(BetSlipEnum.UnSettled) {

    private var earlySettleListener: RecyclerItemListener<BetSlipOrder>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseBetSlipViewHolder<*> {
        val holder = super.onCreateViewHolder(parent, viewType)
        if (holder is BetSlipUnsettledViewHolder) {
            holder.setEarlySettleSubmitListener(object : RecyclerItemListener<String> {
                override fun onItemClick(item: String?, position: Int) {
                    earlySettleListener?.onItemClick(getItem(position) as BetSlipOrder, position)
                }
            })
        }
        return holder
    }

    fun setEarlySettleListener(listener: RecyclerItemListener<BetSlipOrder>) {
        this.earlySettleListener = listener
    }


}