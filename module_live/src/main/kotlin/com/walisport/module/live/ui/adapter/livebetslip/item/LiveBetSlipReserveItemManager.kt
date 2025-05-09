package com.walisport.module.live.ui.adapter.livebetslip.item

import android.annotation.SuppressLint
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.walisport.module.live.R
import com.walisport.module.live.data.constants.LiveBetSlipEnum
import com.walisport.module.live.data.constants.LiveBetSlipExpandedEnum
import com.walisport.module.live.data.model.LiveBetSlipSelectionData
import com.walisport.module.live.databinding.ItemLiveBetSlipReserveBinding
import com.walisport.module.live.utils.LiveDateUtil
import galaxy.common.proto.Common

class LiveBetSlipReserveItemManager(
    private val binding: ItemLiveBetSlipReserveBinding,
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
        item.reserve?.let {
            updateReserveData(it, binding)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateReserveData(
        item: Common.ReserveOrderSelection,
        nBinding: ItemLiveBetSlipReserveBinding
    ) {
        val match = item.matchBasic
        Glide.with(nBinding.root.context).load(match.tournamentIcon).into(nBinding.betReserveIvBall)
        with(nBinding) {
            betReserveTvRace.text = match.matchName
            betReserveTvIntroduce.text = item.selectionName
            betReserveTvAodds.text =
                nBinding.root.context.getString(R.string.live_bet_except_odds, item.odds)
//                TODO 滚球不清楚
            betReserveTvStatus.text = "滚球"
            betReserveTvScore.text = ContextCompat.getString(
                binding.root.context,
                R.string.live_bet_full_handicap
            ) + "  " + match.liveInfo.score
            betReserveTvStart.text =
                LiveDateUtil.getMDHm(match.startTime)
        }
    }

}