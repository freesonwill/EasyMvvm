package arch.cayenne.module.betslip.ui.adapter.livebetslip

import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.module.bet.databinding.AdapterLiveBetSlipReserveBinding
import arch.cayenne.module.betslip.data.constants.LiveBetSlipEnum
import arch.cayenne.module.betslip.data.model.LiveBetSlipData
import arch.cayenne.module.betslip.data.model.LiveBetSlipSelectionData
import arch.cayenne.module.betslip.ui.adapter.LiveBetSlipSelectionAdapter
import arch.cayenne.module.betslip.utisl.LiveBetSlipUtils.expectMaxAmount
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

    override fun covertPlus(position: Int, item: arch.cayenne.module.betslip.data.model.LiveBetSlipData) {
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