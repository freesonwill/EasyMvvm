package arch.cayenne.module.betslip.ui.viewholder.item

import android.annotation.SuppressLint
import androidx.core.view.isVisible
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.common.data.constants.SportEnum
import arch.cayenne.lib.database.entity.BetSlipSelectionData
import arch.cayenne.lib.database.entity.OrderSelectionBean
import arch.cayenne.module.betslip.databinding.ItemLiveBetSlipUnsettleBinding
import arch.cayenne.module.betslip.utisl.BetSlipDateUtil
import com.bumptech.glide.Glide

class BetSlipUnsettledItemViewHolder(binding: ViewBinding):
    BaseBetSlipItemViewHolder<ItemLiveBetSlipUnsettleBinding>(binding) {

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
    private fun updateData(item: OrderSelectionBean) {
        item.let {
            val match = item.matchBasic
            with(mBinding) {
                Glide.with(betUnsettledIvBall.context).load(SportEnum.getSportEnumById(match.sportId)?.resId ?: SportEnum.Default.resId).into(betUnsettledIvBall)
                betUnsettledTvRace.text = match.matchName
                betUnsettledTvIntroduce.text = item.selectionName
                betUnsettledTvAodds.text = "@${item.odds}"
                betUnsettledTvStatus.isVisible = item.inPlay
                val score = item.marketName + "  (${whenScoreIsNull(item.betScore)})"
                betUnsettledTvScore.text = score
                betUnsettledTvStart.text = BetSlipDateUtil.getMDHm(match.startTime)
            }
        }
    }
}