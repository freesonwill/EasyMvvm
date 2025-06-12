package arch.cayenne.module.betslip.ui.adapter.livebetslip

import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.getDetailFormatDate
import arch.cayenne.module.betslip.databinding.AdapterLiveBetSlipReserveBinding
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.data.model.BetSlipData
import arch.cayenne.module.betslip.data.model.BetSlipReserve
import arch.cayenne.module.betslip.data.model.BetSlipReserveSelectionData
import arch.cayenne.module.betslip.data.model.BetSlipSelectionData
import arch.cayenne.module.betslip.data.model.ReserveOrderBean
import arch.cayenne.module.betslip.ui.adapter.BetSlipSelectionAdapter
import arch.cayenne.module.betslip.utisl.BetSlipUtils.expectMaxAmount

class BetSlipReserveAdapterManager(
    private val binding: AdapterLiveBetSlipReserveBinding, private val betSlipType: BetSlipEnum
) : BetSlipBaseAdapterManager(binding) {

    override fun createViewHolder() {
        initRecyclerView(binding.recyclerSelection, betSlipType)
        binding.betReserveBtCancel.clickNoRepeat {
            cancelReserveSubmit((it.tag as Int))
        }
        binding.betReserveBtModify.clickNoRepeat {
            reserveUpdateSubmit((it.tag as Int))
        }
    }

    override fun covertPlus(position: Int, item: BetSlipData) {
        if (item is BetSlipReserve) {
            item.reserve.let {
                updateReserveData(position, it)
                submitReserveAdapter(it)
            }
        }

    }

    /**
     * 预约单数据更新
     * */
    private fun updateReserveData(
        position: Int, order: ReserveOrderBean
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
        reserve: ReserveOrderBean,
    ) {
        val list = listOf(BetSlipReserveSelectionData(reserve = reserve.selection))
        binding.recyclerSelection.adapter?.let {
            val adapter = it as BetSlipSelectionAdapter
            adapter.submitList(list)
        }
    }
}