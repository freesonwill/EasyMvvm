package com.walisport.module.live.ui.adapter.livebetslip

import android.annotation.SuppressLint
import androidx.core.view.isVisible
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.live.data.constants.LiveBetSlipEnum
import com.walisport.module.live.data.model.LiveBetSlipData
import com.walisport.module.live.databinding.AdapterLiveBetSlipUnsettleBinding
import com.walisport.module.live.utils.LiveBetSlipUtils.earlySettlePrice
import com.walisport.module.live.utils.LiveBetSlipUtils.expectMaxAmount
import galaxy.common.proto.Common.Order

class LiveBetSlipUnsettledAdapterManager(
    private val binding: AdapterLiveBetSlipUnsettleBinding, private val betSlipType: LiveBetSlipEnum
) : LiveBetSlipBaseAdapterManager(binding, betSlipType) {

    override fun createViewHolder() {
        initRecyclerView(binding.recyclerSelection, betSlipType)
        binding.betUnsettledBtSettle.clickNoRepeat {
            val position = it.tag as Int
            earlySettledSubmit(position)
        }
        binding.ivTip.clickNoRepeat {
            showBetTip(binding.ivTip)
        }

    }

    override fun covertPlus(position: Int, item: LiveBetSlipData) {
        item.order?.let {
            updateData(it, position)
            submitAdapter(binding.recyclerSelection, item, position)
        }
    }

    /**
     * 未结算 确认中 已结算 更新数据
     * */
    @SuppressLint("SetTextI18n")
    private fun updateData(
        order: Order,
        position: Int
    ) {
        binding.also {
            it.betUnsettledBtSettle.alpha = if (order.earlySupport) 1f else 0.5f
            it.betUnsettledBtSettle.tag = position
            it.betUnsettledTvBetcodeValue.text = order.betId
            it.betUnsettledTvOddsValue.text = order.odds
            it.betUnsettledTvBettingValue.text = order.betAmount
            it.betUnsettledTvExceptValue.text = expectMaxAmount(order.betAmount, order.odds)
            it.betUnsettledTvEarlySettle.let {
                it.text = "$${
                    earlySettlePrice(
                        order.betAmount,
                        order.earlySettlePrice.price,
                        order.earlyBetAmount
                    )
                }"
            }
            val flag = order.comboType != 0  // 0 - 单关 1-串关 2-全窜关
            it.groupCrossborder.isVisible = flag
            if (flag) {
                it.betUnsettledTvCrossborderValue.text = order.parlayName
            }
            it.groupEarlysettle.isVisible = order.earlySupport
            if (order.earlySupport) {
                it.betUnsettledTvEarlysettleValue.text = order.earlyBetAmount
            }
            it.groupBetcode.isVisible = order.selectionsList.size >= 3
        }
    }

}