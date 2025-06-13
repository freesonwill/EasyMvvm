package arch.cayenne.module.betslip.ui.viewholder

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.R
import arch.cayenne.lib.common.ui.adapter.RecyclerItemListener
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.data.constants.BetSlipExpandedEnum
import arch.cayenne.module.betslip.data.model.BetSlipOrder
import arch.cayenne.module.betslip.data.model.BetSlipOrderSelectionData
import arch.cayenne.module.betslip.data.model.BetSlipSelectionData
import arch.cayenne.module.betslip.databinding.ItemTipsLayoutBinding
import arch.cayenne.module.betslip.ui.adapter.BetSlipSelectionAdapter
import arch.cayenne.module.betslip.utisl.BetSlipAdapterMangerInterface

abstract class BaseBetSlipViewHolder<VB: ViewBinding>(binding: ViewBinding) : BaseViewHolder(binding), BetSlipAdapterMangerInterface {

    protected val mBinding: VB get() = binding as VB

    protected lateinit var adapter: BetSlipSelectionAdapter

    fun setExpandedListener(listener: RecyclerItemListener<BetSlipExpandedEnum>?) {
        adapter.setExpandListener(listener)
    }

    fun setLiveListener(listener: RecyclerItemListener<BetSlipSelectionData>?) {
        adapter.setLiveListener(listener)
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
        val pop = PopupWindow(attachView.context)
        pop.contentView =
            ItemTipsLayoutBinding.inflate(LayoutInflater.from(attachView.context)).root
        pop.isOutsideTouchable = true
        pop.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    attachView.context,
                    R.color.tran_0
                )
            )
        )
        pop.showAsDropDown(attachView)
    }
}