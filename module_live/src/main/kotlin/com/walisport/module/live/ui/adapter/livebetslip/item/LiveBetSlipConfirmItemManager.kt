package com.walisport.module.live.ui.adapter.livebetslip.item

import android.annotation.SuppressLint
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.walisport.module.live.R
import com.walisport.module.live.data.constants.LiveBetSlipEnum
import com.walisport.module.live.data.constants.LiveBetSlipExpandedEnum
import com.walisport.module.live.data.model.LiveBetSlipData
import com.walisport.module.live.data.model.LiveBetSlipSelectionData
import com.walisport.module.live.databinding.ItemLiveBetSlipConfirmBinding
import com.walisport.module.live.utils.LiveDateUtil
import galaxy.common.proto.Common

class LiveBetSlipConfirmItemManager(
    private val binding: ItemLiveBetSlipConfirmBinding, private val liveBetSlip: LiveBetSlipEnum
) : LiveBetSlipBaseItemManager(binding, liveBetSlip) {

    override fun createViewHolder() {
        initMoreListener(binding.llMore)
    }

    override fun covertPlus(
        position: Int,
        count: Int,
        expandedEnum: LiveBetSlipExpandedEnum,
        item: LiveBetSlipSelectionData
    ) {
        item.selection?.let { selection ->
            binding.also {
                configView(
                    expandedEnum,
                    it.line,
                    it.groupGradient,
                    it.tvMore,
                    it.ivArrow,
                    position,
                    count,
                    it.llMore
                )
            }
            updateData(selection)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateData(
        item: Common.OrderSelection?
    ) {
        item?.let {
            with(binding) {
                val match = item.matchBasic
                Glide.with(betConfirmIvBall.context).load(match.tournamentIcon)
                    .into(betConfirmIvBall)
                betConfirmTvRace.text = match.matchName
                betConfirmTvIntroduce.text = item.selectionName
                betConfirmTvAodds.text =
                    binding.root.resources.getString(R.string.live_bet_except_odds, "@${item.odds}")
//                betConfirmTvStatus.isVisible = it.inPlay
                betConfirmTvScore.text = item.marketName + "  " + item.betScore
                betConfirmTvStart.text = LiveDateUtil.getMDHm(match.startTime)
            }
        }
    }


}