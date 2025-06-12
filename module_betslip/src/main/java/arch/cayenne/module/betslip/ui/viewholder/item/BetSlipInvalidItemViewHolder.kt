package arch.cayenne.module.betslip.ui.viewholder.item

import android.annotation.SuppressLint
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.common.data.constants.SportEnum
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.data.constants.BetSlipExpandedEnum
import arch.cayenne.module.betslip.data.model.BetSlipOrderSelectionData
import arch.cayenne.module.betslip.data.model.BetSlipSelectionData
import arch.cayenne.module.betslip.databinding.ItemLiveBetSlipInvalidBinding
import arch.cayenne.module.betslip.utisl.BetSlipDateUtil
import com.bumptech.glide.Glide
import galaxy.common.proto.Common

class BetSlipInvalidItemViewHolder(binding: ViewBinding) :
    BaseBetSlipItemViewHolder<ItemLiveBetSlipInvalidBinding>(binding) {

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


    @SuppressLint("SetTextI18n")
    private fun updateData(
        item: Common.OrderSelection
    ) {
        item.let {
            val match = item.matchBasic
            with(mBinding) {
                Glide.with(betInvalidIvBall.context).load(SportEnum.getSportEnumById(match.sportId)?.resId ?: SportEnum.Default.resId).into(betInvalidIvBall)
                betInvalidTvRace.text = match.matchName
                betInvalidTvIntroduce.text = item.selectionName
                betInvalidTvAodds.text = binding.root.resources.getString(
                    R.string.live_bet_except_odds,
                    item.odds
                )
    //                betInvalidTvMatchStatus.isVisible = item.inPlay
                betInvalidTvScore.text = item.marketName + "  (${item.betScore})"
                betInvalidTvStart.text = BetSlipDateUtil.getMDHm(match.startTime)
            }
        }
    }
}