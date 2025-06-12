package arch.cayenne.module.betslip.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.module.betslip.databinding.AdapterLiveBetSlipConfirmBinding
import arch.cayenne.module.betslip.databinding.AdapterLiveBetSlipInvalidBinding
import arch.cayenne.module.betslip.databinding.AdapterLiveBetSlipReserveBinding
import arch.cayenne.module.betslip.databinding.AdapterLiveBetSlipSettledBinding
import arch.cayenne.module.betslip.databinding.AdapterLiveBetSlipUnsettleBinding
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.data.constants.BetSlipExpandedEnum
import arch.cayenne.module.betslip.data.model.BetSlipData
import arch.cayenne.module.betslip.data.model.BetSlipSelectionData
import arch.cayenne.module.betslip.ui.compare.BetSlipCompare
import arch.cayenne.lib.common.ui.adapter.RecyclerItemListener
import arch.cayenne.module.betslip.data.model.BetSlipOrder
import arch.cayenne.module.betslip.data.model.BetSlipReserve
import arch.cayenne.module.betslip.ui.viewholder.BaseBetSlipViewHolder
import arch.cayenne.module.betslip.ui.viewholder.BetSlipConfirmViewHolder
import arch.cayenne.module.betslip.ui.viewholder.BetSlipInvalidViewHolder
import arch.cayenne.module.betslip.ui.viewholder.BetSlipReserveViewHolder
import arch.cayenne.module.betslip.ui.viewholder.BetSlipSettledViewHolder
import arch.cayenne.module.betslip.ui.viewholder.BetSlipUnsettledViewHolder

class BetSlipAdapter(type: BetSlipEnum) :
    BaseAdapter<BetSlipData, BaseBetSlipViewHolder<*>, ViewBinding>(BetSlipCompare()) {
    private val betSlipType = type
    private var earlySettleListener: RecyclerItemListener<BetSlipOrder>? = null
    private var cancelReserveListener: RecyclerItemListener<BetSlipReserve>? = null
    private var modifyReserveListener: RecyclerItemListener<BetSlipReserve>? = null
    private var liveListener: RecyclerItemListener<BetSlipSelectionData>? = null

    fun setEarlySettleListener(listener: RecyclerItemListener<BetSlipOrder>) {
        this.earlySettleListener = listener
    }

    fun setReserveListener(
        cancelListener: RecyclerItemListener<BetSlipReserve>,
        modifyListener: RecyclerItemListener<BetSlipReserve>
    ) {
        this.cancelReserveListener = cancelListener
        this.modifyReserveListener = modifyListener
    }

    fun setLiveListener(liveListener: RecyclerItemListener<BetSlipSelectionData>) {
        this.liveListener = liveListener
    }

    override fun createViewBinding(
        inflater: LayoutInflater, parent: ViewGroup, viewType: Int
    ): ViewBinding {
        return when (betSlipType) {
            BetSlipEnum.UnSettled -> AdapterLiveBetSlipUnsettleBinding.inflate(inflater, parent, false)
            BetSlipEnum.Confirming -> AdapterLiveBetSlipConfirmBinding.inflate(inflater, parent, false)
            BetSlipEnum.Settled -> AdapterLiveBetSlipSettledBinding.inflate(inflater, parent, false)
            BetSlipEnum.Reserve -> AdapterLiveBetSlipReserveBinding.inflate(inflater, parent, false)
            BetSlipEnum.Invalid -> AdapterLiveBetSlipInvalidBinding.inflate(inflater, parent, false)
        }
    }

    override fun createViewHolder(binding: ViewBinding, viewType: Int): BaseBetSlipViewHolder<*> {
        val holder = when (betSlipType) {
            BetSlipEnum.UnSettled -> BetSlipUnsettledViewHolder(binding, betSlipType).apply {
                this.setEarlySettleSubmitListener(object : RecyclerItemListener<String> {
                    override fun onItemClick(item: String?, position: Int) {
                        earlySettleListener?.onItemClick(getItem(position) as BetSlipOrder, position)
                    }
                })
            }
            BetSlipEnum.Confirming -> BetSlipConfirmViewHolder(binding, betSlipType)
            BetSlipEnum.Settled -> BetSlipSettledViewHolder(binding, betSlipType)
            BetSlipEnum.Reserve -> BetSlipReserveViewHolder(binding, betSlipType).apply {
                this.setReserveModifySubmitListener(object : RecyclerItemListener<String> {
                    override fun onItemClick(item: String?, position: Int) {
                        modifyReserveListener?.onItemClick(getItem(position) as BetSlipReserve, position)
                    }
                })
                this.setCancelReserveSubmitListener(object : RecyclerItemListener<String> {
                    override fun onItemClick(item: String?, position: Int) {
                        cancelReserveListener?.onItemClick(getItem(position) as BetSlipReserve, position)
                    }
                })
            }
            BetSlipEnum.Invalid -> BetSlipInvalidViewHolder(binding, betSlipType)
        }
        holder.createViewHolder()
        holder.setExpandedListener(object : RecyclerItemListener<BetSlipExpandedEnum> {
                override fun onItemClick(item: BetSlipExpandedEnum?, position: Int) {
                    val status =
                        if (getItem(position).expandedEnum == BetSlipExpandedEnum.Fold) BetSlipExpandedEnum.Expanded else BetSlipExpandedEnum.Fold
                    currentList[position].expandedEnum = status
                    notifyItemChanged(position)
                }
        })
        holder.setLiveListener(object : RecyclerItemListener<BetSlipSelectionData> {
            override fun onItemClick(item: BetSlipSelectionData?, position: Int) {
                liveListener?.onItemClick(item, position)
            }
        })
        return holder
    }


    override fun convertPlus(holder: BaseBetSlipViewHolder<*>, binding: ViewBinding, position: Int) {
        val data = getItem(position)
        holder.covertPlus(data)
    }
}