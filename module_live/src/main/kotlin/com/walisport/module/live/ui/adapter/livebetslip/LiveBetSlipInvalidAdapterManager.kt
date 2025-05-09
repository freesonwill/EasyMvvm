package com.walisport.module.live.ui.adapter.livebetslip

import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.skin.res.SportSkinResourceManager
import com.walisport.module.live.R
import com.walisport.module.live.data.constants.LiveBetSlipEnum
import com.walisport.module.live.data.constants.LiveBetSlipExpandedEnum
import com.walisport.module.live.data.model.LiveBetSlipData
import com.walisport.module.live.data.model.LiveBetSlipSelectionData
import com.walisport.module.live.databinding.AdapterLiveBetSlipInvalidBinding
import com.walisport.module.live.ui.adapter.LiveBetSlipSelectionAdapter
import com.walisport.module.live.utils.LiveBetSlipUtils.expectMaxAmount
import com.walisport.module.live.utils.RecyclerItemListener
import galaxy.common.proto.Common.Order

class LiveBetSlipInvalidAdapterManager(
    private val binding: AdapterLiveBetSlipInvalidBinding, private val betSlipType: LiveBetSlipEnum
) : LiveBetSlipBaseAdapterManager(binding, betSlipType) {

    override fun createViewHolder() {
        val manager = LinearLayoutManager(binding.root.context)
        val adapter = LiveBetSlipSelectionAdapter(betSlipType,
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
    }

    override fun covertPlus(position: Int, item: LiveBetSlipData) {
        item.order?.let {
            updateData(item.order, false)
        }
        submitAdapter(binding.recyclerSelection,item,position)
    }

    /**
     * 失效更新数据
     * */
    private fun updateData(
        item: Order, isReserve: Boolean
    ) {
        if (isReserve) {
            updateReserve(item)
        } else {
            updateInvalid(item)
        }
    }

    private fun updateReserve(item: Order) {
        with(binding) {
            betExpiredTvStatus.width = 60.dp2px
            betExpiredTvStatus.text =
                root.context.resources.getString(R.string.live_bet_reserve_expired)
            betExpiredTvStatus.setBackgroundResource(
                SportSkinResourceManager.getTargetResourceId(
                    root.context, R.drawable.live_bet_selection_status_light
                )
            )
            tvUnit1.text =
                ContextCompat.getString(binding.root.context, R.string.live_bet_reserve_odds)
            tvUnit2.text =
                ContextCompat.getString(binding.root.context, R.string.live_bet_reserve_bet)
            tvUnit3.text =
                ContextCompat.getString(binding.root.context, R.string.live_bet_except_max_win)
            tvUnit4.isVisible = false
            tvUnit4Value.isVisible = false

            tvUnit1Value.text = item.betId //预约赔率
            tvUnit2Value.text = item.odds  //预约投注
            tvUnit3Value.text = item.betAmount //预约最高可赢
        }
    }

    private fun updateInvalid(item: Order) {
        with(binding) {
            betExpiredTvStatus.width = 34.dp2px
            betExpiredTvStatus.text = root.context.resources.getString(R.string.live_bet_rejection)
            betExpiredTvStatus.setBackgroundResource(
                SportSkinResourceManager.getTargetResourceId(
                    root.context, R.drawable.live_bet_selection_status_normal
                )
            )
            tvUnit1.text = ContextCompat.getString(binding.root.context, R.string.live_bet_bet_num)
            tvUnit2.text = ContextCompat.getString(binding.root.context, R.string.live_bet_odds)
            tvUnit3.text = ContextCompat.getString(binding.root.context, R.string.live_bet_on)
            tvUnit4.text =
                ContextCompat.getString(binding.root.context, R.string.live_bet_except_max_win)
            tvUnit1Value.text = item.betId
            tvUnit2Value.text = item.odds
            tvUnit3Value.text = item.betAmount
            tvUnit4Value.text = expectMaxAmount(item.betAmount, item.odds)
        }
    }
}