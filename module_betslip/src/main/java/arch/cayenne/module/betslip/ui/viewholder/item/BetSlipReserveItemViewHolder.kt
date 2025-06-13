package arch.cayenne.module.betslip.ui.viewholder.item

import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.common.data.constants.SportEnum
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.data.model.BetSlipSelectionData
import arch.cayenne.module.betslip.data.model.ReserveOrderSelectionBean
import arch.cayenne.module.betslip.databinding.ItemLiveBetSlipReserveBinding
import arch.cayenne.module.betslip.utisl.BetSlipDateUtil
import com.bumptech.glide.Glide

class BetSlipReserveItemViewHolder(binding: ViewBinding) :
    BaseBetSlipItemViewHolder<ItemLiveBetSlipReserveBinding>(binding) {

    override fun covertPlus(
        item: BetSlipSelectionData
    ) {
        if (item is ReserveOrderSelectionBean) {
            updateReserveData(item)
            showLiveArrow(item, mBinding.ivCircleArrow)
        }
    }

    private fun updateReserveData(
        item: ReserveOrderSelectionBean
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