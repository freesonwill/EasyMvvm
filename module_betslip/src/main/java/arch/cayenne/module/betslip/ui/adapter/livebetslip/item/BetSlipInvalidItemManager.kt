package arch.cayenne.module.betslip.ui.adapter.livebetslip.item

import android.annotation.SuppressLint
import arch.cayenne.lib.common.data.constants.SportEnum
import com.bumptech.glide.Glide
import galaxy.common.proto.Common
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.databinding.ItemLiveBetSlipInvalidBinding
import arch.cayenne.module.betslip.data.constants.BetSlipExpandedEnum
import arch.cayenne.module.betslip.data.model.BetSlipOrderSelectionData
import arch.cayenne.module.betslip.data.model.BetSlipSelectionData
import arch.cayenne.module.betslip.utisl.BetSlipDateUtil


class BetSlipInvalidItemManager(
    private val binding: ItemLiveBetSlipInvalidBinding,
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
        if (item is BetSlipOrderSelectionData) {
            updateData(item.selection)
        }
        binding.ivCircleArrow.tag = position
    }


    @SuppressLint("SetTextI18n")
    private fun updateData(
        item: Common.OrderSelection?,
    ) {
        item?.let {
            val match = item.matchBasic
            with(binding) {
                Glide.with(betInvalidIvBall.context).load(SportEnum.getSportEnumById(match.sportId)?.resId ?: SportEnum.Default.resId).into(betInvalidIvBall)
                betInvalidTvRace.text = match.matchName
                betInvalidTvIntroduce.text = item.selectionName
                betInvalidTvAodds.text = binding.root.resources.getString(
                    R.string.live_bet_except_odds,
                    "${item.odds}"
                )
//                betInvalidTvMatchStatus.isVisible = item.inPlay
                betInvalidTvScore.text = item.marketName + "  (${item.betScore})"
                betInvalidTvStart.text = BetSlipDateUtil.getMDHm(match.startTime)
            }
        }
    }


}