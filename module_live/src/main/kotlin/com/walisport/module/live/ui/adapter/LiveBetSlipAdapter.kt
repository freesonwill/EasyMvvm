package com.walisport.module.live.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.live.compare.LiveBetSlipCompare
import com.walisport.module.live.data.model.LiveBetSlipAdapterManager
import com.walisport.module.live.data.constants.LiveBetSlipEnum
import com.walisport.module.live.data.model.LiveBetSlipData
import com.walisport.module.live.data.constants.LiveBetSlipExpandedEnum
import com.walisport.module.live.databinding.AdapterLiveBetSlipConfirmBinding
import com.walisport.module.live.databinding.AdapterLiveBetSlipInvalidBinding
import com.walisport.module.live.databinding.AdapterLiveBetSlipReserveBinding
import com.walisport.module.live.databinding.AdapterLiveBetSlipSettledBinding
import com.walisport.module.live.databinding.AdapterLiveBetSlipUnsettleBinding
import com.walisport.module.live.utils.RecyclerItemListener
import galaxy.common.proto.Common
import galaxy.common.proto.Common.Order

class LiveBetSlipAdapter(type: LiveBetSlipEnum) :
    BaseAdapter<LiveBetSlipData, LiveBetSlipAdapter.LiveBetSlipViewHolder, ViewBinding>(
        LiveBetSlipCompare()
    ) {
    private val betSlipType = type
    private var itemListener: RecyclerItemListener<LiveBetSlipData>? = null

    fun setItemListener(listener: RecyclerItemListener<LiveBetSlipData>) {
        this.itemListener = listener
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ViewBinding {
        return when (betSlipType) {
            LiveBetSlipEnum.UnSettled -> AdapterLiveBetSlipUnsettleBinding.inflate(
                inflater,
                parent,
                false
            )

            LiveBetSlipEnum.Confirming -> AdapterLiveBetSlipConfirmBinding.inflate(
                inflater,
                parent,
                false
            )

            LiveBetSlipEnum.Settled -> AdapterLiveBetSlipSettledBinding.inflate(
                inflater,
                parent,
                false
            )

            LiveBetSlipEnum.Reserve -> AdapterLiveBetSlipReserveBinding.inflate(
                inflater,
                parent,
                false
            )

            LiveBetSlipEnum.Invalid -> AdapterLiveBetSlipInvalidBinding.inflate(
                inflater,
                parent,
                false
            )
        }
    }

    override fun createViewHolder(binding: ViewBinding, viewType: Int): LiveBetSlipViewHolder {
        val holder = LiveBetSlipViewHolder(binding)
        //监听投注项是否展开
        holder.manager.init(object : RecyclerItemListener<LiveBetSlipExpandedEnum> {
            override fun onItemClick(item: LiveBetSlipExpandedEnum?, position: Int) {
                val status =
                    if (item == LiveBetSlipExpandedEnum.Fold) LiveBetSlipExpandedEnum.Expanded else LiveBetSlipExpandedEnum.Fold
                currentList[position].expandedEnum = status
                notifyItemChanged(position)
            }
        })
        holder.earlySettled()
        return holder
    }


    override fun convertPlus(holder: LiveBetSlipViewHolder, binding: ViewBinding, position: Int) {

        val item = getItem(position)
        holder.manager.updateView(position, getItem(position))

    }

    private fun getSelections(order: Common.Order) = order.selectionsList

    private fun getMatch(selection: Common.OrderSelection) = selection.matchBasic

    private fun getEarlySettlePrice(order: Order) = order.earlySettlePrice

    inner class LiveBetSlipViewHolder(binding: ViewBinding) : BaseViewHolder(binding) {

        val manager = LiveBetSlipAdapterManager(binding, betSlipType)

        fun earlySettled() {
            if (binding !is AdapterLiveBetSlipUnsettleBinding) {
                return
            }
            val nBinding = binding as AdapterLiveBetSlipUnsettleBinding
            nBinding.betUnsettledBtSettle.clickNoRepeat {
                val position = it.tag as Int
                itemListener?.onItemClick(getItem(position), position)
            }
        }
    }

}