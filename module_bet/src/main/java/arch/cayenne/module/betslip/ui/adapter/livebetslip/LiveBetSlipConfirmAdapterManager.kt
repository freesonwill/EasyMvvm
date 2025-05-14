package arch.cayenne.module.betslip.ui.adapter.livebetslip

import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.module.bet.databinding.AdapterLiveBetSlipConfirmBinding
import arch.cayenne.module.betslip.data.constants.LiveBetSlipEnum
import arch.cayenne.module.betslip.utisl.LiveBetSlipUtils.expectMaxAmount

class LiveBetSlipConfirmAdapterManager(
    private val binding: AdapterLiveBetSlipConfirmBinding,
    private val betSlipType: LiveBetSlipEnum
) : LiveBetSlipBaseAdapterManager(binding, betSlipType) {

    override fun createViewHolder() {
       initRecyclerView(binding.recyclerSelection,betSlipType)
        binding.ivTip.clickNoRepeat {
            showBetTip(it)
        }
    }

    override fun covertPlus(position: Int, item: arch.cayenne.module.betslip.data.model.LiveBetSlipData) {
        updateData(item, position)
        submitAdapter(binding.recyclerSelection, item, position)
    }


    /**
     * 未结算 确认中 已结算 更新数据
     * */
    private fun updateData(
        item: arch.cayenne.module.betslip.data.model.LiveBetSlipData,
        position: Int
    ) {
        binding.also {
            item.order?.let { order ->
                it.betConfirmTvBetcodeValue.text = order.betId
                it.betConfirmTvOddsValue.text = order.odds
                it.betConfirmTvBettingValue.text = order.betAmount
                it.betConfirmTvExceptValue.text = expectMaxAmount(order.betAmount, order.odds)
            }
        }
    }


}