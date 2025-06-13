package arch.cayenne.module.betslip.ui.viewholder.item

import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.common.data.constants.SportEnum
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.data.constants.BetSlipExpandedEnum
import arch.cayenne.module.betslip.data.model.BetSlipOrderSelectionData
import arch.cayenne.module.betslip.data.model.BetSlipSelectionData
import arch.cayenne.module.betslip.databinding.ItemLiveBetSlipConfirmBinding
import arch.cayenne.module.betslip.utisl.BetSlipDateUtil
import com.bumptech.glide.Glide
import galaxy.common.proto.Common

class BetSlipConfirmItemViewHolder(binding: ViewBinding) :
    BaseBetSlipItemViewHolder<ItemLiveBetSlipConfirmBinding>(binding) {

    override fun createViewHolder() {
        initMoreListener(mBinding.ilMore.llMore)
    }

    override fun covertPlus(
        count: Int,
        expandedEnum: BetSlipExpandedEnum,
        item: BetSlipSelectionData
    ) {
        if (item is BetSlipOrderSelectionData) {
            item.selection.let { selection ->
                mBinding.also {
                    configView(
                        expandedEnum,
                        it.line,
                        it.ilMore.groupGradient,
                        it.ilMore.tvMore,
                        it.ilMore.ivArrow,
                        count
                    )
                }
                updateData(selection)
                showLiveArrow(item, mBinding.ivCircleArrow)
            }
        }
    }

    private fun updateData(
        item: Common.OrderSelection
    ) {
        with(mBinding) {
            val match = item.matchBasic
            Glide.with(betConfirmIvBall.context).load(SportEnum.getSportEnumById(match.sportId)?.resId ?: SportEnum.Default.resId).into(betConfirmIvBall)
            betConfirmTvRace.text = match.matchName
            betConfirmTvIntroduce.text = item.selectionName
            betConfirmTvAodds.text =
                binding.root.resources.getString(R.string.live_bet_except_odds, item.odds)
//                betConfirmTvStatus.isVisible = it.inPlay
            val score = item.marketName + "  (${item.betScore})"
            betConfirmTvScore.text = score
            betConfirmTvStart.text = BetSlipDateUtil.getMDHm(match.startTime)
        }
    }
}