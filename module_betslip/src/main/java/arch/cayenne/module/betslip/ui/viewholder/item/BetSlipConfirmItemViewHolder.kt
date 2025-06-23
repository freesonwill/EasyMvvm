package arch.cayenne.module.betslip.ui.viewholder.item

import android.annotation.SuppressLint
import androidx.core.view.isVisible
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.common.data.constants.SportEnum
import arch.cayenne.lib.database.entity.BetSlipSelectionData
import arch.cayenne.lib.database.entity.OrderSelectionBean
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.databinding.ItemLiveBetSlipConfirmBinding
import arch.cayenne.module.betslip.utisl.BetSlipDateUtil
import com.bumptech.glide.Glide

class BetSlipConfirmItemViewHolder(binding: ViewBinding) :
    BaseBetSlipItemViewHolder<ItemLiveBetSlipConfirmBinding>(binding) {
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
        with(mBinding) {
            val match = item.matchBasic
            Glide.with(betConfirmIvBall.context).load(SportEnum.getSportEnumById(match.sportId)?.resId ?: SportEnum.Default.resId).into(betConfirmIvBall)
            betConfirmTvRace.text = match.matchName
            betConfirmTvIntroduce.text = item.selectionName
            betConfirmTvAodds.text =
                binding.root.resources.getString(R.string.live_bet_except_odds, item.odds)
            betConfirmTvStatus.isVisible = item.inPlay
            betConfirmTvScore.text = item.marketName + "  (${whenScoreIsNull(item.betScore)})"
            betConfirmTvStart.text = BetSlipDateUtil.getMDHm(match.startTime)
        }
    }
}