package arch.cayenne.module.betslip.ui.adapter.livebetslip

import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.getDetailFormatDate
import arch.cayenne.module.betslip.databinding.AdapterLiveBetSlipReserveBinding
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.data.model.BetSlipSelectionData
import arch.cayenne.module.betslip.ui.adapter.BetSlipSelectionAdapter
import arch.cayenne.module.betslip.utisl.BetSlipUtils.expectMaxAmount
import galaxy.common.proto.Common.ReserveOrder

class BetSlipReserveAdapterManager(
    private val binding: AdapterLiveBetSlipReserveBinding, private val betSlipType: BetSlipEnum
) : BetSlipBaseAdapterManager(binding, betSlipType) {

    override fun createViewHolder() {
        initRecyclerView(binding.recyclerSelection, betSlipType)
        binding.betReserveBtCancel.clickNoRepeat {
            cancelReserveSubmit((it.tag as Int))
        }
        binding.betReserveBtModify.clickNoRepeat {
            reserveUpdateSubmit((it.tag as Int))
        }
    }

    override fun covertPlus(position: Int, item: arch.cayenne.module.betslip.data.model.BetSlipData) {
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
            betReserveTvDate.text = order.reserveTime.getDetailFormatDate()
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
        val list = arrayListOf(BetSlipSelectionData(reserve = reserve.selection))
        binding.recyclerSelection.adapter?.let {
            val adapter = it as BetSlipSelectionAdapter
            adapter.submitList(list)
        }
    }
}