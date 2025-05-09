package com.walisport.module.live.ui.adapter.livebetslip

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.skin.res.SportSkinResourceManager
import com.walisport.module.live.R
import com.walisport.module.live.data.constants.LiveBetSlipEnum
import com.walisport.module.live.data.constants.LiveBetSlipExpandedEnum
import com.walisport.module.live.data.constants.LiveBetSlipResultOrderStatusEnum
import com.walisport.module.live.data.model.LiveBetSlipData
import com.walisport.module.live.data.model.LiveBetSlipSelectionData
import com.walisport.module.live.databinding.AdapterLiveBetSlipSettledBinding
import com.walisport.module.live.databinding.ItemTipsLayoutBinding
import com.walisport.module.live.ui.adapter.LiveBetSlipSelectionAdapter
import com.walisport.module.live.utils.LiveBetSlipUtils.earlySettlePrice
import com.walisport.module.live.utils.LiveBetSlipUtils.expectMaxAmount
import com.walisport.module.live.utils.LiveBetSlipUtils.winOrLoseAmount
import com.walisport.module.live.utils.RecyclerItemListener
import galaxy.common.proto.Common
import galaxy.common.proto.Common.Order

class LiveBetSlipSettledAdapterManager(
    private val binding: AdapterLiveBetSlipSettledBinding,
    private val betSlipType: LiveBetSlipEnum
) : LiveBetSlipBaseAdapterManager(binding, betSlipType) {

    override fun createViewHolder() {
        initRecyclerView(binding.recyclerSelection, betSlipType)
        binding.ivTip.clickNoRepeat {
            showBetTip(binding.ivTip)
        }
    }

    override fun covertPlus(position: Int, item: LiveBetSlipData) {
        item.order?.let {
            updateData(it)
            submitAdapter(binding.recyclerSelection, item, position)
        }
    }

    /**
     *未结算 确认中 已结算 更新数据
     */
    private fun updateData(order: Order) {
        binding.also {
            it.betSettledTvBetcodeValue.text = order.betId
            it.betSettledTvOddsValue.text = order.odds
            it.betSettledTvBettingValue.text = order.betAmount
            it.betSettledTvExceptValue.text = expectMaxAmount(order.betAmount, order.odds)
            settledStatus(order)
        }
    }

    private fun settledStatus(item: Order) {
        binding.also {
            val status = LiveBetSlipResultOrderStatusEnum.getStatus(item.resultStatus)
            status?.let { st ->
                it.betSettledTvResult.text = st.names
                it.betSettledTvResult.background =
                    SportSkinResourceManager.getDrawable(it.betSettledTvResult.context, st.resId)
            }
        }
    }

}