package arch.cayenne.module.betslip.ui.adapter.livebetslip

import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.getDetailFormatDate
import arch.cayenne.module.betslip.databinding.AdapterLiveBetSlipConfirmBinding
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.data.model.BetSlipData
import arch.cayenne.module.betslip.utisl.BetSlipUtils.expectMaxAmount

class BetSlipConfirmAdapterManager(
    private val binding: AdapterLiveBetSlipConfirmBinding,
    private val betSlipType: BetSlipEnum
) : BetSlipBaseAdapterManager(binding, betSlipType) {

    override fun createViewHolder() {
       initRecyclerView(binding.recyclerSelection,betSlipType)
        binding.ivTip.clickNoRepeat {
            showBetTip(it)
        }
    }

    override fun covertPlus(position: Int, item: BetSlipData) {
        updateData(item, position)
        submitAdapter(binding.recyclerSelection, item, position)
    }


    /**
     * 未结算 确认中 已结算 更新数据
     * */
    private fun updateData(
        item:BetSlipData,
        position: Int
    ) {
        binding.also {
            item.order?.let { order ->
                it.betConfirmTvDate.text = order.betTime.getDetailFormatDate()
                it.betConfirmTvBetcodeValue.text = order.betId
                it.betConfirmTvOddsValue.text = order.odds
                it.betConfirmTvBettingValue.text = order.betAmount
                it.betConfirmTvExceptValue.text = expectMaxAmount(order.betAmount, order.odds)
            }
        }
    }


}