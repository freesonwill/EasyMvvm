package com.walisport.module.live.ui.adapter.livebetslip

import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.live.data.constants.LiveBetSlipEnum
import com.walisport.module.live.data.constants.LiveBetSlipExpandedEnum
import com.walisport.module.live.data.model.LiveBetSlipData
import com.walisport.module.live.data.model.LiveBetSlipSelectionData
import com.walisport.module.live.databinding.AdapterLiveBetSlipReserveBinding
import com.walisport.module.live.ui.adapter.LiveBetSlipSelectionAdapter
import com.walisport.module.live.utils.LiveBetSlipUtils.expectMaxAmount
import com.walisport.module.live.utils.RecyclerItemListener
import galaxy.common.proto.Common.ReserveOrder

class LiveBetSlipReserveAdapterManager(
    private val binding: AdapterLiveBetSlipReserveBinding, private val betSlipType: LiveBetSlipEnum
) : LiveBetSlipBaseAdapterManager(binding, betSlipType) {

    override fun createViewHolder() {
        initRecyclerView(binding.recyclerSelection, betSlipType)
        binding.betReserveBtCancel.clickNoRepeat {
            cancelReserveSubmit((it.tag as Int))
        }
        binding.betReserveBtModify.clickNoRepeat {
            reserveUpdateSubmit((it.tag as Int))
        }
    }

    override fun covertPlus(position: Int, item: LiveBetSlipData) {
        item.reserve?.let {
            updateReserveData(position, it)
            submitReserveAdapter(it)
        }
    }

    /**
     * 预约单数据更新
     * */
    private fun updateReserveData(
        position: Int, order: ReserveOrder
    ) {
        with(binding) {
            val selection = order.selection
            betReserveTvOddsValue.text = selection.odds
            betReserveTvBettingValue.text = order.betAmount
            betReserveTvExceptValue.text = expectMaxAmount(order.betAmount, order.selection.odds)
            betReserveBtCancel.tag = position
            betReserveBtModify.tag = position
        }
    }

    /**
     * 预约单列表展示
     * */
    private fun submitReserveAdapter(
        reserve: ReserveOrder,
    ) {
        val list = arrayListOf(LiveBetSlipSelectionData(reserve = reserve.selection))
        binding.recyclerSelection.adapter?.let {
            val adapter = it as LiveBetSlipSelectionAdapter
            adapter.submitList(list)
        }
    }
}