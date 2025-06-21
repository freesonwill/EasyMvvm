package arch.cayenne.module.betslip.ui.viewholder.item

import android.annotation.SuppressLint
import androidx.core.view.isVisible
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.common.data.constants.SportEnum
import arch.cayenne.lib.database.entity.BetSlipSelectionData
import arch.cayenne.lib.database.entity.ReserveOrderSelectionBean
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.databinding.ItemLiveBetSlipReserveBinding
import arch.cayenne.module.betslip.utisl.BetSlipDateUtil
import com.bumptech.glide.Glide

class BetSlipReserveItemViewHolder(binding: ViewBinding) :
    BaseBetSlipItemViewHolder<ItemLiveBetSlipReserveBinding>(binding) {

    override fun hideLastLine() {
        mBinding.line.isVisible = false
    }

    override fun covertPlus(
        item: BetSlipSelectionData
    ) {
        if (item is ReserveOrderSelectionBean) {
            updateReserveData(item)
            showLiveArrow(item, mBinding.ivCircleArrow)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateReserveData(
        item: ReserveOrderSelectionBean
    ) {
        val match = item.matchBasic
        with(mBinding) {
            Glide.with(betReserveIvBall.context).load(SportEnum.getSportEnumById(match.sportId)?.resId ?: SportEnum.Default.resId).into(betReserveIvBall)
            betReserveTvRace.text = match.matchName
            betReserveTvIntroduce.text = item.selectionName
            betReserveTvAodds.text = itemView.context.getString(R.string.live_bet_except_odds, item.odds)
            betReserveTvScore.text = item.marketName + "  (${whenScoreIsNull(item.liveInfo.score)})"
            betReserveTvStart.text = BetSlipDateUtil.getMDHm(match.startTime)
        }
    }
}