package com.walisport.module.live.ui.adapter.livebetslip

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.live.data.constants.LiveBetSlipEnum
import com.walisport.module.live.data.constants.LiveBetSlipExpandedEnum
import com.walisport.module.live.data.model.LiveBetSlipData
import com.walisport.module.live.databinding.AdapterLiveBetSlipConfirmBinding
import com.walisport.module.live.databinding.ItemTipsLayoutBinding
import com.walisport.module.live.ui.adapter.LiveBetSlipSelectionAdapter
import com.walisport.module.live.utils.LiveBetSlipUtils.expectMaxAmount
import com.walisport.module.live.utils.RecyclerItemListener

class LiveBetSlipConfirmAdapterManager(
    private val binding: AdapterLiveBetSlipConfirmBinding,
    private val betSlipType: LiveBetSlipEnum
) : LiveBetSlipBaseAdapterManager(binding, betSlipType) {

    override fun createViewHolder() {
        val manager = LinearLayoutManager(binding.root.context)
        val adapter = LiveBetSlipSelectionAdapter(
            betSlipType,
            object : RecyclerItemListener<LiveBetSlipExpandedEnum> {
                override fun onItemClick(item: LiveBetSlipExpandedEnum?, position: Int) {
                    expandedListener?.onItemClick(null, position)
                }
            })
        binding.recyclerSelection.also {
            it.layoutManager = manager
            it.itemAnimator = null
            it.adapter = adapter
        }
        binding.ivTip.clickNoRepeat {
            showBetTip(it)
        }
    }

    override fun covertPlus(position: Int, item: LiveBetSlipData) {
        updateData(item, position)
        submitAdapter(binding.recyclerSelection, item, position)
    }


    /**
     * 未结算 确认中 已结算 更新数据
     * */
    private fun updateData(
        item: LiveBetSlipData,
        position: Int
    ) {
        binding.also {
            item.order?.let { order ->
                it.betConfirmTvBetcodeValue.text = order.betId
                it.betConfirmTvOddsValue.text = order.odds
                it.betConfirmTvBettingValue.text = order.betAmount
                it.betConfirmTvExceptValue.text = expectMaxAmount(order.betAmount, order.odds)
            }
        }
    }


}