package arch.cayenne.module.betslip.ui.adapter.livebetslip

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import arch.cayenne.module.bet.databinding.AdapterLiveBetSlipConfirmBinding
import arch.cayenne.module.bet.databinding.AdapterLiveBetSlipInvalidBinding
import arch.cayenne.module.bet.databinding.AdapterLiveBetSlipReserveBinding
import arch.cayenne.module.bet.databinding.AdapterLiveBetSlipSettledBinding
import arch.cayenne.module.bet.databinding.AdapterLiveBetSlipUnsettleBinding
import arch.cayenne.module.bet.databinding.ItemTipsLayoutBinding
import arch.cayenne.module.betslip.data.constants.BetSlipExpandedEnum
import arch.cayenne.module.betslip.data.model.LiveBetSlipSelectionData
import arch.cayenne.module.betslip.ui.adapter.BetSlipSelectionAdapter
import arch.cayenne.module.betslip.utisl.BetSlipAdapterMangerInterface
import arch.cayenne.module.betslip.utisl.RecyclerItemListener

abstract class BetSlipBaseAdapterManager(
    private val binding: ViewBinding,
    private val liveBetSlip: arch.cayenne.module.betslip.data.constants.BetSlipEnum
) : BetSlipAdapterMangerInterface {
    var expandedListener: RecyclerItemListener<BetSlipExpandedEnum>? = null
    var earlySettleSubmitListener: RecyclerItemListener<String>? = null
    var cancelReserveSubmitListener: RecyclerItemListener<String>? = null
    var reserveModifySubmitListener: RecyclerItemListener<String>? = null


    companion object {

        fun initManager(
            binding: ViewBinding,
            liveBetSlip: arch.cayenne.module.betslip.data.constants.BetSlipEnum
        ): BetSlipBaseAdapterManager? {
            return when (binding) {
                is AdapterLiveBetSlipUnsettleBinding -> BetSlipUnsettledAdapterManager(
                    binding,
                    liveBetSlip
                )

                is AdapterLiveBetSlipConfirmBinding -> BetSlipConfirmAdapterManager(
                    binding,
                    liveBetSlip
                )

                is AdapterLiveBetSlipSettledBinding -> BetSlipSettledAdapterManager(
                    binding,
                    liveBetSlip
                )

                is AdapterLiveBetSlipReserveBinding -> BetSlipReserveAdapterManager(
                    binding,
                    liveBetSlip
                )

                is AdapterLiveBetSlipInvalidBinding -> BetSlipInvalidAdapterManager(
                    binding,
                    liveBetSlip
                )

                else -> null
            }
        }
    }

    fun initRecyclerView(recyclerView:RecyclerView,betSlip: arch.cayenne.module.betslip.data.constants.BetSlipEnum){
        val manager = LinearLayoutManager(binding.root.context)
        val adapter = BetSlipSelectionAdapter(
            betSlip,
            object : RecyclerItemListener<BetSlipExpandedEnum> {
                override fun onItemClick(item: BetSlipExpandedEnum?, position: Int) {
                    expandedListener?.onItemClick(null, position)
                }
            })
        recyclerView.also {
            it.layoutManager = manager
            it.itemAnimator = null
            it.adapter = adapter
        }
    }

    /**
     * 投注单列表展示
     * */
    fun submitAdapter(
        recyclerView: RecyclerView, data: arch.cayenne.module.betslip.data.model.BetSlipData, position: Int
    ) {
        var list =
            data.order!!.selectionsList.map { LiveBetSlipSelectionData(selection = it) }.toList()

        recyclerView.adapter?.let {
            val adapter = it as BetSlipSelectionAdapter
            adapter.updateBasicData(data.expandedEnum, position)
            if (list.size > 3 && data.expandedEnum == BetSlipExpandedEnum.Fold) {
                list = list.subList(0, 3)
            }
            adapter.submitList(list)
        }
    }

    fun earlySettledSubmit(position: Int) {
        earlySettleSubmitListener?.onItemClick("", position)
    }

    fun cancelReserveSubmit(position: Int) {
        cancelReserveSubmitListener?.onItemClick("", position)
    }

    fun reserveUpdateSubmit(position: Int) {
        reserveModifySubmitListener?.onItemClick("", position)
    }

    fun showBetTip(attachView: View) {
        val pop = PopupWindow(attachView.context)
        pop.contentView =
            ItemTipsLayoutBinding.inflate(LayoutInflater.from(attachView.context)).root
        pop.isOutsideTouchable = true
        pop.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    attachView.context,
                    arch.cayenne.lib.common.R.color.tran_0
                )
            )
        )
        pop.showAsDropDown(attachView)
    }
}