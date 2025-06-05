package arch.cayenne.module.betslip.ui.adapter.livebetslip

import android.annotation.SuppressLint
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import arch.cayenne.lib.common.ui.view.ProgressDrawable
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.getDetailFormatDate
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.data.model.BetSlipData
import arch.cayenne.module.betslip.data.model.BetSlipOrder
import arch.cayenne.module.betslip.data.model.OrderBean
import arch.cayenne.module.betslip.databinding.AdapterLiveBetSlipUnsettleBinding
import arch.cayenne.module.betslip.utisl.BetSlipUtils.calculateMinSettlePrice
import arch.cayenne.module.betslip.utisl.BetSlipUtils.earlySettlePrice
import arch.cayenne.module.betslip.utisl.BetSlipUtils.expectMaxAmount

class BetSlipUnsettledAdapterManager(
    private val binding: AdapterLiveBetSlipUnsettleBinding, private val betSlipType: BetSlipEnum
) : BetSlipBaseAdapterManager(binding) {

    override fun createViewHolder() {
        initRecyclerView(binding.recyclerSelection, betSlipType)
        binding.ivTip.clickNoRepeat {
            showBetTip(binding.ivTip)
        }

    }

    override fun covertPlus(position: Int, item: BetSlipData) {
        if (item is BetSlipOrder) {
            item.order.let {
                updateData(it, position)
                submitAdapter(binding.recyclerSelection, item, position)
            }
        }
    }

    /**
     * 未结算 确认中 已结算 更新数据
     * */
    @SuppressLint("SetTextI18n")
    private fun updateData(
        order: OrderBean,
        position: Int
    ) {
        binding.also {
            it.betUnsettledTvDate.text = order.betTime.getDetailFormatDate()

            // 支援提前結算 且 仍有可結算次數 且 可結算金額大於等於最小結算金額
            it.betUnsettledBtSettle.isEnabled =
                order.earlySupport && order.earlySettleTimes < order.earlySettlePrice.settleTotal && calculateMinSettlePrice(
                    order.betAmount, order.earlyBetAmount, order.earlySettlePrice.settleMin
                )
            it.betUnsettledBtSettle.tag = position
            it.betUnsettledTvBetcodeValue.text = order.betId
            it.betUnsettledTvOddsValue.text = order.odds
            it.betUnsettledTvBettingValue.text = order.betAmount
            it.betUnsettledTvExceptValue.text = expectMaxAmount(order.betAmount, order.odds)
            it.betUnsettledBtAmount.text =
                "$${earlySettlePrice(order.betAmount, order.earlyBetAmount)}"
            val flag = order.comboType != 0  // 0 - 单关 1-串关 2-全窜关
            it.groupCrossborder.isVisible = flag
            if (flag) {
                it.betUnsettledTvCrossborderValue.text = order.parlayName
            }
            it.groupEarlysettle.isVisible = order.earlySupport
            if (order.earlySupport) {
                it.betUnsettledTvEarlysettleValue.text = order.earlyBetAmount
            }
            binding.betUnsettledBtSettle.clickNoRepeat {
                if (order.earlySettlePrice.settleStatus != 102) {
                    earlySettledSubmit(position)
                }
            }
        }
        earlySettleStatus(order.earlySettlePrice.settleStatus)
    }

    /**
     *当提前结算单在提前结算中时提前结算按钮显示为提前结算中
     * */
    private fun earlySettleStatus(settleStatus: Int) {
        binding.also {
            if (settleStatus == 102) {
                it.betUnsettledBtTv.text =
                    ContextCompat.getString(it.root.context, R.string.live_bet_in_early_settle)
                it.betUnsettledBtAmount.isVisible = false
                it.betUnsettledBtProgress.isVisible = true
                it.betUnsettledBtProgress.setImageDrawable(ProgressDrawable())
            } else {
                it.betUnsettledBtTv.text =
                    ContextCompat.getString(it.root.context, R.string.live_bet_early_settle)
                it.betUnsettledBtAmount.isVisible = true
                it.betUnsettledBtProgress.isVisible = false
            }
        }

    }

}