package arch.cayenne.module.betslip.ui.adapter.livebetslip.item

import android.annotation.SuppressLint
import arch.cayenne.module.betslip.databinding.ItemLiveBetSlipReserveBinding
import arch.cayenne.module.betslip.data.model.BetSlipSelectionData
import arch.cayenne.module.betslip.utisl.BetSlipDateUtil
import com.bumptech.glide.Glide
import galaxy.common.proto.Common
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.data.constants.BetSlipExpandedEnum

class BetSlipReserveItemManager(
    private val binding: ItemLiveBetSlipReserveBinding,
    private val liveBetSlip: BetSlipEnum
) : BetSlipBaseItemManager(binding, liveBetSlip) {

    override fun createViewHolder() {
        initMoreListener(binding.ilMore.llMore)
        showLiveArrow(binding.ivCircleArrow)
    }

    override fun covertPlus(
        position: Int,
        count: Int,
        expandedEnum: BetSlipExpandedEnum,
        item: BetSlipSelectionData
    ) {
        binding.also {
            configView(
                expandedEnum,
                it.line,
                it.ilMore.groupGradient,
                it.ilMore.tvMore,
                it.ilMore.ivArrow,
                position,
                count,
                it.ilMore.llMore
            )
        }
        item.reserve?.let {
            updateReserveData(it, binding)
        }
        binding.ivCircleArrow.tag = position
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
            betReserveTvAodds.text = nBinding.root.context.getString(R.string.live_bet_except_odds, item.odds)
//            betReserveTvStatus.isVisible = item.inPlay
            betReserveTvScore.text = item.marketName + "  (${ match.liveInfo.score})"
            betReserveTvStart.text = BetSlipDateUtil.getMDHm(match.startTime)
        }
    }

}