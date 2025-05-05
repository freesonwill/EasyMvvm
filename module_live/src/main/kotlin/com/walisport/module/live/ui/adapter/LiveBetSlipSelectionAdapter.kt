package com.walisport.module.live.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import com.walisport.module.live.compare.LiveBetSlipSelectionCompare
import com.walisport.module.live.data.constants.LiveBetSlipExpandedEnum
import com.walisport.module.live.data.constants.LiveBetSlipEnum
import com.walisport.module.live.data.model.LiveBetSlipSelectionAdapterManager
import com.walisport.module.live.data.model.LiveBetSlipSelectionData
import com.walisport.module.live.databinding.ItemLiveBetSlipConfirmBinding
import com.walisport.module.live.databinding.ItemLiveBetSlipInvalidBinding
import com.walisport.module.live.databinding.ItemLiveBetSlipReserveBinding
import com.walisport.module.live.databinding.ItemLiveBetSlipSettledBinding
import com.walisport.module.live.databinding.ItemLiveBetSlipUnsettleBinding
import com.walisport.module.live.utils.RecyclerItemListener

class LiveBetSlipSelectionAdapter(
    betSlipType: LiveBetSlipEnum,
    val expandListener: RecyclerItemListener<LiveBetSlipExpandedEnum>? = null
) :
    BaseAdapter<LiveBetSlipSelectionData, LiveBetSlipSelectionAdapter.LiveBetSlipSelectionViewHolder, ViewBinding>(
        LiveBetSlipSelectionCompare()
    ) {
    private val betType = betSlipType
    private var expandEnum: LiveBetSlipExpandedEnum = LiveBetSlipExpandedEnum.Hide
    private var parentPosition: Int = -1


    fun updateBasicData(flag: LiveBetSlipExpandedEnum, parentPosition: Int) {
        this.expandEnum = flag
        this.parentPosition = parentPosition
    }

    override fun convertPlus(
        holder: LiveBetSlipSelectionViewHolder, binding: ViewBinding, position: Int
    ) {
        holder.manager.updateView(position, itemCount, expandEnum, getItem(position))
    }

    override fun createViewBinding(
        inflater: LayoutInflater, parent: ViewGroup, viewType: Int
    ): ViewBinding {
        return when (betType) {
            LiveBetSlipEnum.UnSettled -> ItemLiveBetSlipUnsettleBinding.inflate(
                inflater, parent, false
            )

            LiveBetSlipEnum.Confirming -> ItemLiveBetSlipConfirmBinding.inflate(
                inflater, parent, false
            )

            LiveBetSlipEnum.Settled -> ItemLiveBetSlipSettledBinding.inflate(
                inflater, parent, false
            )

            LiveBetSlipEnum.Reserve -> ItemLiveBetSlipReserveBinding.inflate(
                inflater, parent, false
            )

            LiveBetSlipEnum.Invalid -> ItemLiveBetSlipInvalidBinding.inflate(
                inflater, parent, false
            )
        }

    }

    override fun createViewHolder(
        binding: ViewBinding, viewType: Int
    ): LiveBetSlipSelectionViewHolder {
        val holder = LiveBetSlipSelectionViewHolder(binding)
        holder.manager.initListener(object : RecyclerItemListener<LiveBetSlipSelectionData> {
            override fun onItemClick(item: LiveBetSlipSelectionData?, position: Int) {
                expandListener?.onItemClick(expandEnum, parentPosition)
            }
        })
        return holder
    }

    inner class LiveBetSlipSelectionViewHolder(binding: ViewBinding) : BaseViewHolder(binding) {

        val manager = LiveBetSlipSelectionAdapterManager(binding, type = betType)
    }


}