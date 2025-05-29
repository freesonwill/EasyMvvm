package arch.cayenne.module.betslip.ui.adapter.livebetslip

import androidx.core.content.ContextCompat
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.getDetailFormatDate
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.betslip.databinding.AdapterLiveBetSlipSettledBinding
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.data.constants.BetSlipResultOrderStatusEnum
import arch.cayenne.module.betslip.data.model.BetSlipData
import arch.cayenne.module.betslip.utisl.BetSlipUtils.expectMaxAmount
import arch.cayenne.module.betslip.utisl.BetSlipUtils.winOrLoseAmount
import galaxy.common.proto.Common.Order

class BetSlipSettledAdapterManager(
    private val binding: AdapterLiveBetSlipSettledBinding,
    private val betSlipType: BetSlipEnum
) : BetSlipBaseAdapterManager(binding, betSlipType) {

    override fun createViewHolder() {
        initRecyclerView(binding.recyclerSelection, betSlipType)
        binding.ivTip.clickNoRepeat {
            showBetTip(binding.ivTip)
        }
    }

    override fun covertPlus(position: Int, item: BetSlipData) {
        item.order?.let {
            updateData(it)
            submitAdapter(binding.recyclerSelection, item, position)
        }
    }

    /**
     *未结算 确认中 已结算 更新数据
     */
    private fun updateData(order: Order) {
        binding.also {
            it.betSettledTvDate.text = order.betTime.getDetailFormatDate()
            it.betSettledTvBetcodeValue.text = order.betId
            it.betSettledTvOddsValue.text = order.odds
            it.betSettledTvBettingValue.text = order.betAmount
            val amount = winOrLoseAmount(order.betAmount,order.earlyBetAmount,order.returnAmount)
            settledStatus(order)
            it.betSettledTvExceptValue.text = "$amount"
            it.betSettledTvExceptValue.setTextColor(
                ContextCompat.getColorStateList(
                    binding.root.context,
                    if (amount >= 0) arch.cayenne.lib.res.R.color.win_color else arch.cayenne.lib.res.R.color.lose_color
                )
            )
        }
    }

    private fun settledStatus(item: Order) {
        binding.also {
            val status = BetSlipResultOrderStatusEnum.getStatus(item.resultStatus)
            status?.let { st ->
                it.betSettledTvResult.text =  ContextCompat.getString(it.betSettledTvResult.context,st.names)
                it.betSettledTvResult.background = SkinnableResourceManager.getDrawable(it.betSettledTvResult.context,st.resId)
            }
        }
    }

}