package arch.cayenne.module.betslip.ui.viewholder.item

import android.annotation.SuppressLint
import androidx.core.view.isVisible
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.common.data.constants.SportEnum
import arch.cayenne.lib.database.entity.BetSlipSelectionData
import arch.cayenne.lib.database.entity.OrderSelectionBean
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.databinding.ItemLiveBetSlipInvalidBinding
import arch.cayenne.module.betslip.utisl.BetSlipDateUtil
import com.bumptech.glide.Glide

class BetSlipInvalidItemViewHolder(binding: ViewBinding) :
    BaseBetSlipItemViewHolder<ItemLiveBetSlipInvalidBinding>(binding) {

    override fun hideLastLine(isLast:Boolean) {
        mBinding.line.isVisible = !isLast
    }

    override fun covertPlus(
        item: BetSlipSelectionData
    ) {
        if (item is OrderSelectionBean) {
            updateData(item)
            showLiveArrow(item, mBinding.ivCircleArrow)
        }
    }


    @SuppressLint("SetTextI18n")
    private fun updateData(
        item: OrderSelectionBean
    ) {
        item.let {
            val match = item.matchBasic
            with(mBinding) {
                Glide.with(betInvalidIvBall.context).load(SportEnum.getSportEnumById(match.sportId)?.resId ?: SportEnum.Default.resId).into(betInvalidIvBall)
                betInvalidTvRace.text = match.matchName
                betInvalidTvIntroduce.text = item.selectionName
                betInvalidTvAodds.text = expectOdds(binding.root.resources.getString(R.string.live_bet_except_odds, item.odds))
                betInvalidTvMatchStatus.isVisible = item.inPlay
                betInvalidTvScore.text = item.marketName + "  (${whenScoreIsNull(item.betScore)})"
                betInvalidTvStart.text = BetSlipDateUtil.getMDHm(match.startTime)
            }
        }
    }
}