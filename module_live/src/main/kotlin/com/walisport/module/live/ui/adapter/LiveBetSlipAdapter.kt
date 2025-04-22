package com.walisport.module.live.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.adapter.BaseAdapter
import arch.cayenne.lib.base.viewholder.BaseViewHolder
import com.walisport.module.live.compare.LiveBetSlipCompare
import com.walisport.module.live.data.model.LiveBetSlipEnum
import com.walisport.module.live.databinding.AdapterLiveBetSlipConfirmBinding
import com.walisport.module.live.databinding.AdapterLiveBetSlipInvalidBinding
import com.walisport.module.live.databinding.AdapterLiveBetSlipReserveBinding
import com.walisport.module.live.databinding.AdapterLiveBetSlipUnsettleBinding
import com.walisport.module.live.databinding.AdapterLiveBetSlitSettledBinding
import com.walisport.module.live.databinding.FragmentLiveBetslipConfirmBinding
import com.walisport.module.live.databinding.FragmentLiveBetslipInvalidBinding
import com.walisport.module.live.databinding.FragmentLiveBetslipReserveBinding
import com.walisport.module.live.databinding.FragmentLiveBetslipSettledLayoutBinding
import com.walisport.module.live.databinding.FragmentLiveBetslipUnsettledBinding
import galaxy.common.proto.Common

class LiveBetSlipAdapter(type: LiveBetSlipEnum) :
    BaseAdapter<Common.Order, LiveBetSlipAdapter.LiveBetSlipViewHolder, ViewBinding>(
        LiveBetSlipCompare()
    ) {
    private val betSlipType = type

    inner class LiveBetSlipViewHolder(binding: ViewBinding) : BaseViewHolder(binding) {}

    override fun convertPlus(holder: LiveBetSlipViewHolder, binding: ViewBinding, position: Int) {
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

            LiveBetSlipEnum.Settled -> AdapterLiveBetSlitSettledBinding.inflate(
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
        return holder
    }
}