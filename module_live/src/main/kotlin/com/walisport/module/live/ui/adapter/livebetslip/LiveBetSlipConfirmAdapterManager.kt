package com.walisport.module.live.ui.adapter.livebetslip

import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.live.data.constants.LiveBetSlipEnum
import com.walisport.module.live.data.model.LiveBetSlipData
import com.walisport.module.live.databinding.AdapterLiveBetSlipConfirmBinding
import com.walisport.module.live.utils.LiveBetSlipUtils.expectMaxAmount

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

    override fun covertPlus(position: Int, item: LiveBetSlipData) {
        updateData(item, position)
        submitAdapter(binding.recyclerSelection, item, position)
    }


    /**
     * 未结算 确认中 已结算 更新数据
     * */
    private fun updateData(
        item: LiveBetSlipData,
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