package com.walisport.module.live.ui.adapter.livebetslip.item

import android.annotation.SuppressLint
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.walisport.module.live.R
import com.walisport.module.live.data.constants.LiveBetSlipEnum
import com.walisport.module.live.data.constants.LiveBetSlipExpandedEnum
import com.walisport.module.live.data.model.LiveBetSlipSelectionData
import com.walisport.module.live.databinding.ItemLiveBetSlipInvalidBinding
import com.walisport.module.live.ui.adapter.livebetslip.LiveBetSlipBaseAdapterManager
import com.walisport.module.live.utils.LiveDateUtil
import galaxy.common.proto.Common

class LiveBetSlipInvalidItemManager(
    private val binding: ItemLiveBetSlipInvalidBinding,
    private val liveBetSlip: LiveBetSlipEnum
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
        item.selection?.let {
            updateData(it)
        }
    }


    @SuppressLint("SetTextI18n")
    private fun updateData(
        item: Common.OrderSelection?,
    ) {
        item?.let {
            val match = item.matchBasic
            with(binding) {
                Glide.with(betInvalidIvBall.context).load(match.tournamentIcon)
                    .into(betInvalidIvBall)
                betInvalidTvRace.text = match.matchName
                betInvalidTvIntroduce.text = item.selectionName
                betInvalidTvAodds.text = binding.root.resources.getString(
                    R.string.live_bet_except_odds,
                    "@${item.odds}"
                )
//                betInvalidTvMatchStatus.isVisible = item.inPlay
                betInvalidTvScore.text = item.marketName + "  " + item.betScore
                betInvalidTvStart.text = LiveDateUtil.getMDHm(match.startTime)
            }
        }
    }


}