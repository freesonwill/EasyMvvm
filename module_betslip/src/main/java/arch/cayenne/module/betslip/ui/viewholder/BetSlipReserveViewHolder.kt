package arch.cayenne.module.betslip.ui.viewholder

import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.common.ui.adapter.RecyclerItemListener
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.getDetailFormatDate
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.data.model.BetSlipData
import arch.cayenne.module.betslip.data.model.ReserveOrderBean
import arch.cayenne.module.betslip.data.model.toReserveOrderSelectionBean
import arch.cayenne.module.betslip.databinding.AdapterLiveBetSlipReserveBinding
import arch.cayenne.module.betslip.utisl.BetSlipUtils

class BetSlipReserveViewHolder(binding: ViewBinding, private val betSlipType: BetSlipEnum) :
    BaseBetSlipViewHolder<AdapterLiveBetSlipReserveBinding>(binding) {
    private var cancelReserveSubmitListener: RecyclerItemListener<String>? = null
    private var reserveModifySubmitListener: RecyclerItemListener<String>? = null

    override fun createViewHolder() {
        initItemView(mBinding.recyclerSelection, betSlipType)
        mBinding.betReserveBtCancel.clickNoRepeat {
            cancelReserveSubmitListener?.onItemClick("", (it.tag as Int))
        }
        mBinding.betReserveBtModify.clickNoRepeat {
            reserveModifySubmitListener?.onItemClick("", (it.tag as Int))
        }
    }

    override fun covertPlus(item: BetSlipData) {
        if (item is ReserveOrderBean) {
            updateData(item)
            submitReserveData(item)
        }
    }

    fun setCancelReserveSubmitListener(listener: RecyclerItemListener<String>) {
        cancelReserveSubmitListener = listener
    }

    fun setReserveModifySubmitListener(listener: RecyclerItemListener<String>) {
        reserveModifySubmitListener = listener
    }

    /**
     * 预约单数据更新
     * */
    private fun updateData(
        order: ReserveOrderBean
    ) {
        with(mBinding) {
            val selection = order.selection
            betReserveTvDate.text = order.reserveTime.getDetailFormatDate()
            betReserveTvOddsValue.text = selection.odds
            val betAmount = "${moneySymbol}${order.betAmount}"
            betReserveTvBettingValue.text = betAmount
            val exceptAmount = "${moneySymbol}${BetSlipUtils.expectMaxAmount(order.betAmount, selection.odds)}"
            betReserveTvExceptValue.text = exceptAmount
            betReserveBtCancel.tag = adapterPosition
            betReserveBtModify.tag = adapterPosition
        }
    }

    /**
     * 预约单列表展示
     * */
    private fun submitReserveData(
        reserve: ReserveOrderBean,
    ) {
        val list = listOf(reserve.selection.toReserveOrderSelectionBean())
        adapter.submitList(list)
    }
}