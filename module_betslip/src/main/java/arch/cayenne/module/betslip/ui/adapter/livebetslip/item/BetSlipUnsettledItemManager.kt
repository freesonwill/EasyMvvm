package arch.cayenne.module.betslip.ui.adapter.livebetslip.item

import android.annotation.SuppressLint
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import com.bumptech.glide.Glide
import arch.cayenne.module.betslip.databinding.ItemLiveBetSlipUnsettleBinding
import arch.cayenne.module.betslip.data.constants.BetSlipExpandedEnum
import arch.cayenne.module.betslip.data.model.BetSlipSelectionData
import arch.cayenne.module.betslip.utisl.BetSlipDateUtil
import galaxy.common.proto.Common

class BetSlipUnsettledItemManager(
    private val binding: ItemLiveBetSlipUnsettleBinding,
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
        item.selection?.let {
            updateData(it)
        }
        binding.ivCircleArrow.tag = position
    }

    @SuppressLint("SetTextI18n")
    private fun updateData(item: Common.OrderSelection?) {
        item?.let {
            val match = item.matchBasic
            with(binding) {
                Glide.with(betUnsettledIvBall.context).load(match.tournamentIcon)
                    .into(betUnsettledIvBall)
                betUnsettledTvRace.text = match.matchName
                betUnsettledTvIntroduce.text = item.selectionName
                betUnsettledTvAodds.text = "@${item.odds}"
//                betUnsettledTvStatus.isVisible = item.inPlay
                betUnsettledTvScore.text = item.marketName + "  (${item.betScore})"
                betUnsettledTvStart.text = BetSlipDateUtil.getMDHm(match.startTime)
            }
        }
    }
}