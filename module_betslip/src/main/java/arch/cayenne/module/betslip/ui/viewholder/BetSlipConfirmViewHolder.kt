package arch.cayenne.module.betslip.ui.viewholder

import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.getDetailFormatDate
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.data.model.BetSlipData
import arch.cayenne.module.betslip.data.model.BetSlipOrder
import arch.cayenne.module.betslip.databinding.AdapterLiveBetSlipConfirmBinding
import arch.cayenne.module.betslip.utisl.BetSlipUtils

class BetSlipConfirmViewHolder(binding: ViewBinding, private val betSlipType: BetSlipEnum) :
    BaseBetSlipViewHolder<AdapterLiveBetSlipConfirmBinding>(binding) {

    // Additional methods or properties can be added here if needed
    override fun createViewHolder() {
        initItemView(mBinding.recyclerSelection, betSlipType)
        mBinding.ivTip.clickNoRepeat {
            showBetTip(it)
        }
    }

    override fun covertPlus(item: BetSlipData) {
        if (item is BetSlipOrder) {
            updateData(item)
            submitOrderData(item)
        }
    }

    /**
     * 未结算 确认中 已结算 更新数据
     * */
    private fun updateData(
        item: BetSlipOrder
    ) {
        mBinding.also {
            item.order.let { order ->
                it.betConfirmTvDate.text = order.betTime.getDetailFormatDate()
                it.betConfirmTvBetcodeValue.text = order.betId
                it.betConfirmTvOddsValue.text = order.odds
                val betAmount = "${moneySymbol}${order.betAmount}"
                it.betConfirmTvBettingValue.text = betAmount
                val exceptAmount = "${moneySymbol}${BetSlipUtils.expectMaxAmount(order.betAmount, order.odds)}"
                it.betConfirmTvExceptValue.text = exceptAmount
            }
        }
    }
}