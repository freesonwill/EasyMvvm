package arch.cayenne.module.betslip.ui.adapter.livebetslip.item

import android.annotation.SuppressLint
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import arch.cayenne.module.bet.databinding.ItemLiveBetSlipSettledBinding
import arch.cayenne.module.betslip.data.constants.LiveBetSlipResultOrderStatusEnum
import arch.cayenne.module.betslip.data.model.LiveBetSlipSelectionData
import com.bumptech.glide.Glide
import galaxy.common.proto.Common
import arch.cayenne.module.bet.R
import arch.cayenne.module.betslip.data.constants.LiveBetSlipExpandedEnum

class LiveBetSlipSettledItemManager(
    private val binding: ItemLiveBetSlipSettledBinding, private val liveBetSlip: arch.cayenne.module.betslip.data.constants.LiveBetSlipEnum
) : LiveBetSlipBaseItemManager(binding, liveBetSlip) {

    override fun createViewHolder() {
        initMoreListener(binding.ilMore.llMore)
    }

    override fun covertPlus(
        position: Int,
        count: Int,
        expandedEnum: LiveBetSlipExpandedEnum,
        item: LiveBetSlipSelectionData
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
        item.selection?.let {
            updateData(it)
            settledStatus(it)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateData(item: Common.OrderSelection?) {
        item?.let {
            val match = item.matchBasic
            with(binding) {
                Glide.with(betSettledIvBall.context).load(match.tournamentIcon)
                    .into(betSettledIvBall)
                betSettledTvRace.text = match.matchName
                betSettledTvIntroduce.text = item.selectionName
                betSettledTvAodds.text = "@${item.odds}"
//                betSettledTvStatus.isVisible = it.inPlay
                betSettledTvScore.text = item.marketName + "  " + item.betScore
                betSettledTvScore1.text = item.endScore
            }
        }
    }

    private fun settledStatus(item: Common.OrderSelection) {
        binding.also {
            when (val status = LiveBetSlipResultOrderStatusEnum.getStatus(item.status)) {
                LiveBetSlipResultOrderStatusEnum.Win, LiveBetSlipResultOrderStatusEnum.Lose -> {
                    it.iv1.isVisible = true
                    it.betSettledTvStatus1.isVisible = false
                    val resId =
                        if (status == LiveBetSlipResultOrderStatusEnum.Win) R.drawable.icon_betslip_tick else R.drawable.icon_betslip_fork
                    it.iv1.setImageResource(resId)
                }

                LiveBetSlipResultOrderStatusEnum.WinHalf, LiveBetSlipResultOrderStatusEnum.UnSettled, LiveBetSlipResultOrderStatusEnum.Cancel, LiveBetSlipResultOrderStatusEnum.Tie, LiveBetSlipResultOrderStatusEnum.LoseHalf -> {
                    it.iv1.isVisible = false
                    it.betSettledTvStatus1.isVisible = true
                    it.betSettledTvStatus1.text = ContextCompat.getString(it.betSettledTvStatus1.context,status.names)
                }

                else -> {}
            }
        }
    }

}