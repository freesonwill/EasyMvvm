package arch.cayenne.module.betslip.ui.viewholder.item

import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.common.data.constants.SportEnum
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.data.constants.BetSlipExpandedEnum
import arch.cayenne.module.betslip.data.model.BetSlipReserveSelectionData
import arch.cayenne.module.betslip.data.model.BetSlipSelectionData
import arch.cayenne.module.betslip.databinding.ItemLiveBetSlipReserveBinding
import arch.cayenne.module.betslip.utisl.BetSlipDateUtil
import com.bumptech.glide.Glide
import galaxy.common.proto.Common

class BetSlipReserveItemViewHolder(binding: ViewBinding) :
    BaseBetSlipItemViewHolder<ItemLiveBetSlipReserveBinding>(binding) {

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
        if (item is BetSlipReserveSelectionData) {
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
                updateReserveData(item.reserve)
                it.ivCircleArrow.tag = position
            }
        }
    }

    private fun updateReserveData(
        item: Common.ReserveOrderSelection
    ) {
        val match = item.matchBasic
        with(mBinding) {
            Glide.with(betReserveIvBall.context).load(SportEnum.getSportEnumById(match.sportId)?.resId ?: SportEnum.Default.resId).into(betReserveIvBall)
            betReserveTvRace.text = match.matchName
            betReserveTvIntroduce.text = item.selectionName
            betReserveTvAodds.text = itemView.context.getString(R.string.live_bet_except_odds, item.odds)
//            betReserveTvStatus.isVisible = item.inPlay
            val score = item.marketName + "  (${ match.liveInfo.score})"
            betReserveTvScore.text = score
            betReserveTvStart.text = BetSlipDateUtil.getMDHm(match.startTime)
        }
    }
}