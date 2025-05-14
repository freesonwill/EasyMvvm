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
import arch.cayenne.module.betslip.data.constants.LiveBetSlipExpandedEnum
import arch.cayenne.module.betslip.data.model.LiveBetSlipSelectionData
import arch.cayenne.module.betslip.ui.adapter.LiveBetSlipSelectionAdapter
import arch.cayenne.module.betslip.utisl.LiveBetSlipAdapterMangerInterface
import arch.cayenne.module.betslip.utisl.RecyclerItemListener

abstract class LiveBetSlipBaseAdapterManager(
    private val binding: ViewBinding,
    private val liveBetSlip: arch.cayenne.module.betslip.data.constants.LiveBetSlipEnum
) : LiveBetSlipAdapterMangerInterface {
    var expandedListener: RecyclerItemListener<LiveBetSlipExpandedEnum>? = null
    var earlySettleSubmitListener: RecyclerItemListener<String>? = null
    var cancelReserveSubmitListener: RecyclerItemListener<String>? = null
    var reserveModifySubmitListener: RecyclerItemListener<String>? = null


    companion object {

        fun initManager(
            binding: ViewBinding,
            liveBetSlip: arch.cayenne.module.betslip.data.constants.LiveBetSlipEnum
        ): LiveBetSlipBaseAdapterManager? {
            return when (binding) {
                is AdapterLiveBetSlipUnsettleBinding -> LiveBetSlipUnsettledAdapterManager(
                    binding,
                    liveBetSlip
                )

                is AdapterLiveBetSlipConfirmBinding -> LiveBetSlipConfirmAdapterManager(
                    binding,
                    liveBetSlip
                )

                is AdapterLiveBetSlipSettledBinding -> LiveBetSlipSettledAdapterManager(
                    binding,
                    liveBetSlip
                )

                is AdapterLiveBetSlipReserveBinding -> LiveBetSlipReserveAdapterManager(
                    binding,
                    liveBetSlip
                )

                is AdapterLiveBetSlipInvalidBinding -> LiveBetSlipInvalidAdapterManager(
                    binding,
                    liveBetSlip
                )

                else -> null
            }
        }
    }

    fun initRecyclerView(recyclerView:RecyclerView,betSlip: arch.cayenne.module.betslip.data.constants.LiveBetSlipEnum){
        val manager = LinearLayoutManager(binding.root.context)
        val adapter = LiveBetSlipSelectionAdapter(
            betSlip,
            object : RecyclerItemListener<LiveBetSlipExpandedEnum> {
                override fun onItemClick(item: LiveBetSlipExpandedEnum?, position: Int) {
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
        recyclerView: RecyclerView, data: arch.cayenne.module.betslip.data.model.LiveBetSlipData, position: Int
    ) {
        var list =
            data.order!!.selectionsList.map { LiveBetSlipSelectionData(selection = it) }.toList()

        recyclerView.adapter?.let {
            val adapter = it as LiveBetSlipSelectionAdapter
            adapter.updateBasicData(data.expandedEnum, position)
            if (list.size > 3 && data.expandedEnum == LiveBetSlipExpandedEnum.Fold) {
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