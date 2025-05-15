package arch.cayenne.module.betslip.ui.adapter.livebetslip.item

import android.annotation.SuppressLint
import com.bumptech.glide.Glide
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.databinding.ItemLiveBetSlipConfirmBinding
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.data.constants.BetSlipExpandedEnum
import arch.cayenne.module.betslip.data.model.LiveBetSlipSelectionData
import arch.cayenne.module.betslip.utisl.BetSlipDateUtil
import galaxy.common.proto.Common

class BetSlipConfirmItemManager(
    private val binding: ItemLiveBetSlipConfirmBinding, private val liveBetSlip: BetSlipEnum
) : BetSlipBaseItemManager(binding, liveBetSlip) {

    override fun createViewHolder() {
        initMoreListener(binding.ilMore.llMore)
    }

    override fun covertPlus(
        position: Int,
        count: Int,
        expandedEnum: BetSlipExpandedEnum,
        item: LiveBetSlipSelectionData
    ) {
        item.selection?.let { selection ->
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
                    binding.root.resources.getString(R.string.live_bet_except_odds, item.odds)
//                betConfirmTvStatus.isVisible = it.inPlay
                betConfirmTvScore.text = item.marketName + "  (${item.betScore})"
                betConfirmTvStart.text = BetSlipDateUtil.getMDHm(match.startTime)
            }
        }
    }


}