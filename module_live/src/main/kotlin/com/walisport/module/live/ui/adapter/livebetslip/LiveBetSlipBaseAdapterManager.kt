package com.walisport.module.live.ui.adapter.livebetslip

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.walisport.module.live.data.constants.LiveBetSlipEnum
import com.walisport.module.live.data.constants.LiveBetSlipExpandedEnum
import com.walisport.module.live.data.model.LiveBetSlipData
import com.walisport.module.live.data.model.LiveBetSlipSelectionData
import com.walisport.module.live.databinding.AdapterLiveBetSlipConfirmBinding
import com.walisport.module.live.databinding.AdapterLiveBetSlipInvalidBinding
import com.walisport.module.live.databinding.AdapterLiveBetSlipReserveBinding
import com.walisport.module.live.databinding.AdapterLiveBetSlipSettledBinding
import com.walisport.module.live.databinding.AdapterLiveBetSlipUnsettleBinding
import com.walisport.module.live.databinding.ItemTipsLayoutBinding
import com.walisport.module.live.ui.adapter.LiveBetSlipSelectionAdapter
import com.walisport.module.live.utils.LiveBetSlipAdapterMangerInterface
import com.walisport.module.live.utils.RecyclerItemListener

abstract class LiveBetSlipBaseAdapterManager(
    private val binding: ViewBinding,
    private val liveBetSlip: LiveBetSlipEnum
) : LiveBetSlipAdapterMangerInterface {
    var expandedListener: RecyclerItemListener<LiveBetSlipExpandedEnum>? = null
    var earlySettleSubmitListener: RecyclerItemListener<String>? = null
    var cancelReserveSubmitListener: RecyclerItemListener<String>? = null
    var reserveModifySubmitListener: RecyclerItemListener<String>? = null


    companion object {

        fun initManager(
            binding: ViewBinding,
            liveBetSlip: LiveBetSlipEnum
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

    /**
     * 投注单列表展示
     * */
    fun submitAdapter(
        recyclerView: RecyclerView, data: LiveBetSlipData, position: Int
    ) {
        var list =
            data.order!!.selectionsList.map { LiveBetSlipSelectionData(selection = it) }.toList()
        recyclerView.adapter?.let {
            val adapter = it as LiveBetSlipSelectionAdapter
            adapter.updateBasicData(data.expandedEnum, position)
            if (list.size > 3 && data.expandedEnum == LiveBetSlipExpandedEnum.Fold) {
                list = list.subList(0, 2)
            }
            adapter.currentList.clear()
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