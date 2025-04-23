package com.walisport.module.live.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.Group
import androidx.core.view.isVisible
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.adapter.BaseAdapter
import arch.cayenne.lib.base.viewholder.BaseViewHolder
import arch.cayenne.lib.skin.widget.SportView
import com.walisport.module.live.compare.LiveBetSlipSelectionCompare
import com.walisport.module.live.data.model.LiveBetSlipEnum
import com.walisport.module.live.databinding.ItemLiveBetSlipConfirmBinding
import com.walisport.module.live.databinding.ItemLiveBetSlipInvalidBinding
import com.walisport.module.live.databinding.ItemLiveBetSlipReserveBinding
import com.walisport.module.live.databinding.ItemLiveBetSlipSettledBinding
import com.walisport.module.live.databinding.ItemLiveBetSlipUnsettleBinding
import galaxy.common.proto.Common

class LiveBetSlipSelectionAdapter(betSlipType: LiveBetSlipEnum, val showGradient: Boolean) :
    BaseAdapter<Common.OrderSelection, LiveBetSlipSelectionAdapter.LiveBetSlipSelectionViewHolder, ViewBinding>(
        LiveBetSlipSelectionCompare()
    ) {
    val betType = betSlipType
    val gradient = showGradient

    inner class LiveBetSlipSelectionViewHolder(binding: ViewBinding) : BaseViewHolder(binding) {

        fun initView(position: Int, count: Int) {
            when (binding) {
                is ItemLiveBetSlipUnsettleBinding -> {
                    val nBinding = binding as ItemLiveBetSlipUnsettleBinding
                    configView(nBinding.line, nBinding.groupGradient, position, count)
                }

                is ItemLiveBetSlipConfirmBinding -> {
                    val nBinding = binding as ItemLiveBetSlipConfirmBinding
                    configView(nBinding.line, nBinding.groupGradient, position, count)
                }

                is ItemLiveBetSlipSettledBinding -> {
                    val nBinding = binding as ItemLiveBetSlipSettledBinding
                    configView(nBinding.line, nBinding.groupGradient, position, count)
                }

                is ItemLiveBetSlipReserveBinding -> {
                    val nBinding = binding as ItemLiveBetSlipReserveBinding
                    configView(nBinding.line, nBinding.groupGradient, position, count)
                }

                is ItemLiveBetSlipInvalidBinding -> {
                    val nBinding = binding as ItemLiveBetSlipInvalidBinding
                    configView(nBinding.line, nBinding.groupGradient, position, count)
                }
            }
        }

        private fun configView(line: SportView, group: Group, position: Int, count: Int) {
            line.isVisible = position != count - 1
            group.isVisible = position == 2 && gradient
        }


    }

    override fun convertPlus(
        holder: LiveBetSlipSelectionViewHolder,
        binding: ViewBinding,
        position: Int
    ) {
        holder.initView(position, itemCount)
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ViewBinding {
        return when (betType) {
            LiveBetSlipEnum.UnSettled -> ItemLiveBetSlipUnsettleBinding.inflate(
                inflater,
                parent,
                false
            )

            LiveBetSlipEnum.Confirming -> ItemLiveBetSlipConfirmBinding.inflate(
                inflater,
                parent,
                false
            )

            LiveBetSlipEnum.Settled -> ItemLiveBetSlipSettledBinding.inflate(
                inflater,
                parent,
                false
            )

            LiveBetSlipEnum.Reserve -> ItemLiveBetSlipReserveBinding.inflate(
                inflater,
                parent,
                false
            )

            LiveBetSlipEnum.Invalid -> ItemLiveBetSlipInvalidBinding.inflate(
                inflater,
                parent,
                false
            )
        }

    }

    override fun createViewHolder(
        binding: ViewBinding,
        viewType: Int
    ): LiveBetSlipSelectionViewHolder {
        val holder = LiveBetSlipSelectionViewHolder(binding)
        return holder
    }


}