package arch.cayenne.module.betslip.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.database.entity.BetSlipData
import arch.cayenne.lib.database.entity.BetSlipSelectionData
import arch.cayenne.module.betslip.databinding.AdapterLiveBetSlipConfirmBinding
import arch.cayenne.module.betslip.databinding.AdapterLiveBetSlipInvalidBinding
import arch.cayenne.module.betslip.databinding.AdapterLiveBetSlipReserveBinding
import arch.cayenne.module.betslip.databinding.AdapterLiveBetSlipSettledBinding
import arch.cayenne.module.betslip.databinding.AdapterLiveBetSlipUnsettleBinding
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.ui.compare.BetSlipCompare
import arch.cayenne.module.betslip.ui.viewholder.BaseBetSlipViewHolder
import arch.cayenne.module.betslip.ui.viewholder.BetSlipConfirmViewHolder
import arch.cayenne.module.betslip.ui.viewholder.BetSlipInvalidViewHolder
import arch.cayenne.module.betslip.ui.viewholder.BetSlipReserveViewHolder
import arch.cayenne.module.betslip.ui.viewholder.BetSlipSettledViewHolder
import arch.cayenne.module.betslip.ui.viewholder.BetSlipUnsettledViewHolder

open class BetSlipAdapter(private val betSlipType: BetSlipEnum) :
    BaseAdapter<BetSlipData, BaseBetSlipViewHolder<*>, ViewBinding>(BetSlipCompare()) {

    private var liveListener: BetSlipLiveListener? = null

    fun setLiveListener(liveListener: BetSlipLiveListener) {
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
            BetSlipEnum.UnSettled -> BetSlipUnsettledViewHolder(binding, betSlipType)
            BetSlipEnum.Confirming -> BetSlipConfirmViewHolder(binding, betSlipType)
            BetSlipEnum.Settled -> BetSlipSettledViewHolder(binding, betSlipType)
            BetSlipEnum.Reserve -> BetSlipReserveViewHolder(binding, betSlipType)
            BetSlipEnum.Invalid -> BetSlipInvalidViewHolder(binding, betSlipType)
        }
        holder.createViewHolder()
//        holder.setExpandedListener(object : RecyclerItemListener<BetSlipExpandedEnum> {
//                override fun onItemClick(item: BetSlipExpandedEnum?, position: Int) {
//                    val holderPosition = holder.adapterPosition
//                    Log.d("abcd", " $$$ $position  $holderPosition")
//                    val status =
//                        if (getItem(holderPosition).expandedEnum == BetSlipExpandedEnum.Fold) BetSlipExpandedEnum.Expanded else BetSlipExpandedEnum.Fold
//                    currentList[holderPosition].expandedEnum = status
//                    notifyItemChanged(holderPosition)
//                }
//        })
        holder.setLiveListener(liveListener)
        return holder
    }


    override fun convertPlus(holder: BaseBetSlipViewHolder<*>, binding: ViewBinding, position: Int) {
        val data = getItem(position)
        holder.covertPlus(data)
    }

    interface BetSlipLiveListener {
        fun isShowLiveButton(): Boolean
        fun onLiveButtonClick(data: BetSlipSelectionData)
    }

}