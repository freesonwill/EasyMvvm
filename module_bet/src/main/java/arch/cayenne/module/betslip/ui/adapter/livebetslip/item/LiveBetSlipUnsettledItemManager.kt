package arch.cayenne.module.betslip.ui.adapter.livebetslip.item

import android.annotation.SuppressLint
import com.bumptech.glide.Glide
import arch.cayenne.module.bet.R
import arch.cayenne.module.bet.databinding.ItemLiveBetSlipUnsettleBinding
import arch.cayenne.module.betslip.data.constants.LiveBetSlipExpandedEnum
import arch.cayenne.module.betslip.data.model.LiveBetSlipSelectionData
import arch.cayenne.module.betslip.utisl.LiveDateUtil
import galaxy.common.proto.Common

class LiveBetSlipUnsettledItemManager(
    private val binding: ItemLiveBetSlipUnsettleBinding,
    private val liveBetSlip: arch.cayenne.module.betslip.data.constants.LiveBetSlipEnum
) : LiveBetSlipBaseItemManager(binding, liveBetSlip) {

    override fun createViewHolder() {
        initMoreListener(binding.ilMore.llMore)
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
                betUnsettledTvScore.text = item.marketName + "  " + item.betScore
                betUnsettledTvStart.text = LiveDateUtil.getMDHm(match.startTime)
            }
        }
    }
}