package arch.cayenne.module.betslip.ui.viewholder

import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.common.data.constants.CurrencySymbols
import arch.cayenne.lib.common.ui.adapter.RecyclerItemListener
import arch.cayenne.lib.common.utils.ViewUtils
import arch.cayenne.lib.common.utils.ext.SportIntExt.stringToMoney
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.getDetailFormatDate
import arch.cayenne.lib.database.entity.BetSlipData
import arch.cayenne.lib.database.entity.BetSlipReserveBean
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.databinding.AdapterLiveBetSlipReserveBinding
import arch.cayenne.module.betslip.ui.adapter.BetSlipReserveAdapter
import arch.cayenne.module.betslip.utisl.BetSlipUtils

class BetSlipReserveViewHolder(binding: ViewBinding, betSlipType: BetSlipEnum) :
    BaseBetSlipViewHolder<AdapterLiveBetSlipReserveBinding>(binding, betSlipType) {
    private var cancelReserveSubmitListener: RecyclerItemListener<String>? = null
    private var reserveModifySubmitListener: BetSlipReserveAdapter.BetSlipReserveListener? = null

    override fun createViewHolder() {
        initItemView(mBinding.recyclerSelection)
        mBinding.betReserveBtCancel.clickNoRepeat {
            cancelReserveSubmitListener?.onItemClick("", adapterPosition)
        }
        mBinding.betReserveBtModify.clickNoRepeat { view ->
            reserveModifySubmitListener?.let {
                val h = ViewUtils.getStatusBarHeight(view.context)
                val location = IntArray(2)
                view.getLocationInWindow(location)
                reserveModifySubmitListener?.onModifyReserveClick(
                    location.first() + view.width / 2,
                    location.last() - h,
                    view.height,
                    adapterPosition
                )
            }

        }
    }

    override fun covertPlus(item: BetSlipData) {
        if (item is BetSlipReserveBean) {
            updateData(item)
            submitReserveData(item)
        }
    }

    fun setCancelReserveSubmitListener(listener: RecyclerItemListener<String>) {
        cancelReserveSubmitListener = listener
    }

    fun setReserveModifySubmitListener(listener: BetSlipReserveAdapter.BetSlipReserveListener) {
        reserveModifySubmitListener = listener
    }

    /**
     * 预约单数据更新
     * */
    private fun updateData(
        order: BetSlipReserveBean
    ) {
        with(mBinding) {
            val selection = order.selection
            betReserveTvDate.text = order.reserveTime.getDetailFormatDate()
            betReserveTvOddsValue.text = selection.odds
            val betAmount = "${CurrencySymbols.getSymbol(order.currency)}${order.betAmount.stringToMoney()}"
            betReserveTvBettingValue.text = betAmount
            val exceptAmount = "${CurrencySymbols.getSymbol(order.currency)}${BetSlipUtils.expectMaxAmount(order.betAmount, selection.odds)}"
            betReserveTvExceptValue.text = exceptAmount
            betReserveBtCancel.tag = adapterPosition
            betReserveBtModify.tag = adapterPosition
        }
    }

    /**
     * 预约单列表展示
     * */
    private fun submitReserveData(
        reserve: BetSlipReserveBean,
    ) {
        val list = listOf(reserve.selection)
        adapter.submitList(list)
    }
}