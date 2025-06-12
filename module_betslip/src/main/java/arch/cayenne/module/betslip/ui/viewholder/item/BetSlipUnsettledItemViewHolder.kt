package arch.cayenne.module.betslip.ui.viewholder.item

import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.common.data.constants.SportEnum
import arch.cayenne.module.betslip.data.constants.BetSlipExpandedEnum
import arch.cayenne.module.betslip.data.model.BetSlipOrderSelectionData
import arch.cayenne.module.betslip.data.model.BetSlipSelectionData
import arch.cayenne.module.betslip.databinding.ItemLiveBetSlipUnsettleBinding
import arch.cayenne.module.betslip.utisl.BetSlipDateUtil
import com.bumptech.glide.Glide
import galaxy.common.proto.Common

class BetSlipUnsettledItemViewHolder(binding: ViewBinding):
    BaseBetSlipItemViewHolder<ItemLiveBetSlipUnsettleBinding>(binding) {
    override fun createViewHolder() {
        initMoreListener(mBinding.ilMore.llMore)
        showLiveArrow(mBinding.ivCircleArrow)
    }

    override fun covertPlus(
        position: Int,
        count: Int,
        expandedEnum: BetSlipExpandedEnum,
        item: BetSlipSelectionData
    ) {
        if (item is BetSlipOrderSelectionData) {
            mBinding.also {
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
                it.ivCircleArrow.tag = position
            }
            updateData(item.selection)
        }
    }

    private fun updateData(item: Common.OrderSelection) {
        item.let {
            val match = item.matchBasic
            with(mBinding) {
                Glide.with(betUnsettledIvBall.context).load(SportEnum.getSportEnumById(match.sportId)?.resId ?: SportEnum.Default.resId).into(betUnsettledIvBall)
                betUnsettledTvRace.text = match.matchName
                betUnsettledTvIntroduce.text = item.selectionName
                val odds = "@${item.odds}"
                betUnsettledTvAodds.text = odds
    //                betUnsettledTvStatus.isVisible = item.inPlay
                val score = item.marketName + "  (${item.betScore})"
                betUnsettledTvScore.text = score
                betUnsettledTvStart.text = BetSlipDateUtil.getMDHm(match.startTime)
            }
        }
    }
}