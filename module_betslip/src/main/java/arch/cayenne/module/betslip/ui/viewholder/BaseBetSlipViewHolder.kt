package arch.cayenne.module.betslip.ui.viewholder

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.ui.adapter.RecyclerItemListener
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.data.constants.BetSlipExpandedEnum
import arch.cayenne.module.betslip.data.model.BetSlipOrder
import arch.cayenne.module.betslip.data.model.BetSlipOrderSelectionData
import arch.cayenne.module.betslip.ui.adapter.BetSlipAdapter
import arch.cayenne.module.betslip.ui.adapter.BetSlipSelectionAdapter
import arch.cayenne.module.betslip.ui.helper.BetTipsHelper
import arch.cayenne.module.betslip.utisl.BetSlipAdapterViewHolderInterface

abstract class BaseBetSlipViewHolder<VB: ViewBinding>(binding: ViewBinding) : BaseViewHolder(binding), BetSlipAdapterViewHolderInterface {

    protected val mBinding: VB get() = binding as VB

    protected lateinit var adapter: BetSlipSelectionAdapter
    private var betSlipListener: BetSlipAdapter.BetSlipListener? = null
    protected val moneySymbol: String
        get() = betSlipListener?.getMoneySymbol() ?: ""

    fun setExpandedListener(listener: RecyclerItemListener<BetSlipExpandedEnum>?) {
        adapter.setExpandListener(listener)
    }

    fun setLiveListener(listener: BetSlipAdapter.BetSlipLiveListener?) {
        adapter.setLiveListener(listener)
    }

    fun setBetSlipListener(listener: BetSlipAdapter.BetSlipListener?) {
        betSlipListener = listener
    }

    protected fun initItemView(recyclerView: RecyclerView, betSlip: BetSlipEnum) {
        val manager = LinearLayoutManager(recyclerView.context)
        adapter = BetSlipSelectionAdapter(
            betSlip
        )
        recyclerView.also {
            it.layoutManager = manager
            it.itemAnimator = null
            it.adapter = adapter
        }
    }

    protected fun submitOrderData(
        data: BetSlipOrder
    ) {
        var list =
            data.order.selectionsList.map { BetSlipOrderSelectionData(selection = it) }.toList()

        adapter.let {
            adapter.updateBasicData(data.expandedEnum, this.adapterPosition)
            if (list.size > 3 && data.expandedEnum == BetSlipExpandedEnum.Fold) {
                list = list.subList(0, 3)
            }
            adapter.submitList(list)
        }
    }

    protected fun showBetTip(attachView: View) {
        val helper = BetTipsHelper()
        helper.showTips(attachView)
    }
}