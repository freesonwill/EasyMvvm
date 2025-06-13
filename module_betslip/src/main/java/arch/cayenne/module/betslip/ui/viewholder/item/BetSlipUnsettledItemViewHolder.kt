package arch.cayenne.module.betslip.ui.viewholder.item

import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.common.data.constants.SportEnum
import arch.cayenne.module.betslip.data.model.BetSlipSelectionData
import arch.cayenne.module.betslip.data.model.OrderSelectionBean
import arch.cayenne.module.betslip.databinding.ItemLiveBetSlipUnsettleBinding
import arch.cayenne.module.betslip.utisl.BetSlipDateUtil
import com.bumptech.glide.Glide

class BetSlipUnsettledItemViewHolder(binding: ViewBinding):
    BaseBetSlipItemViewHolder<ItemLiveBetSlipUnsettleBinding>(binding) {

    override fun covertPlus(
        item: BetSlipSelectionData
    ) {
        if (item is OrderSelectionBean) {
            updateData(item)
            showLiveArrow(item, mBinding.ivCircleArrow)
        }
    }

    private fun updateData(item: OrderSelectionBean) {
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