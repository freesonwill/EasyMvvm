package arch.cayenne.module.betslip.ui.viewholder.item

import android.annotation.SuppressLint
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.common.data.constants.SportEnum
import arch.cayenne.lib.database.entity.BetSlipSelectionData
import arch.cayenne.lib.database.entity.OrderSelectionBean
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.data.constants.BetSlipResultOrderStatusEnum
import arch.cayenne.module.betslip.databinding.ItemLiveBetSlipSettledBinding
import com.bumptech.glide.Glide

class BetSlipSettledItemViewHolder(binding: ViewBinding) :
    BaseBetSlipItemViewHolder<ItemLiveBetSlipSettledBinding>(binding) {

    override fun hideLastLine(isLast:Boolean) {
        mBinding.line.isVisible = !isLast
    }

    override fun covertPlus(
        item: BetSlipSelectionData
    ) {
        if (item is OrderSelectionBean) {
            updateData(item)
            settledStatus(item)
            showLiveArrow(item, mBinding.ivCircleArrow)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateData(item: OrderSelectionBean) {
        item.let {
            val match = item.matchBasic
            with(mBinding) {
                Glide.with(betSettledIvBall.context).load(SportEnum.getSportEnumById(match.sportId)?.resId ?: SportEnum.Default.resId).into(betSettledIvBall)
                betSettledTvRace.text = match.matchName
                betSettledTvIntroduce.text = item.selectionName
                betSettledTvAodds.text = expectOdds( "@${item.odds}")
                betSettledTvStatus.isVisible = it.inPlay
                betSettledTvScore.text =  item.marketName + "  (${whenScoreIsNull(item.betScore)})"
                betSettledTvScore1.text = whenScoreIsNull(item.endScore)
            }
        }
    }

    private fun settledStatus(item: OrderSelectionBean) {
        mBinding.also {
            when (val status = BetSlipResultOrderStatusEnum.getStatus(item.status)) {
                BetSlipResultOrderStatusEnum.Win, BetSlipResultOrderStatusEnum.Lose -> {
                    it.iv1.isVisible = true
                    it.betSettledTvStatus1.isVisible = false
                    val resId =
                        if (status == BetSlipResultOrderStatusEnum.Win) R.drawable.icon_betslip_tick else R.drawable.icon_betslip_fork
                    it.iv1.setImageResource(resId)
                }

                BetSlipResultOrderStatusEnum.WinHalf, BetSlipResultOrderStatusEnum.UnSettled, BetSlipResultOrderStatusEnum.Cancel, BetSlipResultOrderStatusEnum.Tie, BetSlipResultOrderStatusEnum.LoseHalf -> {
                    it.iv1.isVisible = false
                    it.betSettledTvStatus1.isVisible = true
                    it.betSettledTvStatus1.text = ContextCompat.getString(it.betSettledTvStatus1.context,status.names)
                }

                else -> {}
            }
        }
    }

}