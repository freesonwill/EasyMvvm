package com.walisport.module.live.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import com.walisport.module.live.compare.LiveBetSlipSelectionCompare
import com.walisport.module.live.data.model.LiveBetSlipEnum
import com.walisport.module.live.data.livebetslip.LiveBetSlipSelectionAdapterManager
import com.walisport.module.live.data.livebetslip.LiveBetSlipSelectionData
import com.walisport.module.live.databinding.ItemLiveBetSlipConfirmBinding
import com.walisport.module.live.databinding.ItemLiveBetSlipInvalidBinding
import com.walisport.module.live.databinding.ItemLiveBetSlipReserveBinding
import com.walisport.module.live.databinding.ItemLiveBetSlipSettledBinding
import com.walisport.module.live.databinding.ItemLiveBetSlipUnsettleBinding
import com.walisport.module.live.utils.RecyclerItemListener

class LiveBetSlipSelectionAdapter(betSlipType: LiveBetSlipEnum) :
    BaseAdapter<LiveBetSlipSelectionData, LiveBetSlipSelectionAdapter.LiveBetSlipSelectionViewHolder, ViewBinding>(
        LiveBetSlipSelectionCompare()
    ) {
    private val betType = betSlipType
    private var gradient = false
    private var itemListener: RecyclerItemListener<LiveBetSlipSelectionData>? = null


    fun updateGradient(flag: Boolean) {
        this.gradient = flag
    }

    fun setItemListener(listener: RecyclerItemListener<LiveBetSlipSelectionData>) {
        this.itemListener = listener
    }

    override fun convertPlus(
        holder: LiveBetSlipSelectionViewHolder, binding: ViewBinding, position: Int
    ) {
        holder.manager.updateView(position, itemCount, false, getItem(position))
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
                itemListener?.onItemClick(getItem(position), position)
            }
        })
        return holder
    }

    inner class LiveBetSlipSelectionViewHolder(binding: ViewBinding) : BaseViewHolder(binding) {

        val manager = LiveBetSlipSelectionAdapterManager(binding, type = betType)
    }


}