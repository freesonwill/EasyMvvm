package arch.cayenne.module.betslip.ui.adapter.livebetslip

import android.annotation.SuppressLint
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import arch.cayenne.lib.common.ui.view.ProgressDrawable
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.getDetailFormatDate
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.databinding.AdapterLiveBetSlipUnsettleBinding
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.data.model.BetSlipData
import arch.cayenne.module.betslip.utisl.BetSlipUtils.earlySettlePrice
import arch.cayenne.module.betslip.utisl.BetSlipUtils.expectMaxAmount
import galaxy.common.proto.Common.Order

class BetSlipUnsettledAdapterManager(
    private val binding: AdapterLiveBetSlipUnsettleBinding, private val betSlipType: BetSlipEnum
) : BetSlipBaseAdapterManager(binding, betSlipType) {

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

    override fun covertPlus(position: Int, item: BetSlipData) {
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
            it.betUnsettledTvDate.text = order.betTime.getDetailFormatDate()
            it.betUnsettledBtSettle.alpha = if (order.earlySupport) 1f else 0.5f
            it.betUnsettledBtSettle.tag = position
            it.betUnsettledTvBetcodeValue.text = order.betId
            it.betUnsettledTvOddsValue.text = order.odds
            it.betUnsettledTvBettingValue.text = order.betAmount
            it.betUnsettledTvExceptValue.text = expectMaxAmount(order.betAmount, order.odds)
            it.betUnsettledBtAmount.text = "$${earlySettlePrice(order.betAmount, order.earlyBetAmount)}"
            val flag = order.comboType != 0  // 0 - 单关 1-串关 2-全窜关
            it.groupCrossborder.isVisible = flag
            if (flag) {
                it.betUnsettledTvCrossborderValue.text = order.parlayName
            }
            it.groupEarlysettle.isVisible = order.earlySupport
            if (order.earlySupport) {
                it.betUnsettledTvEarlysettleValue.text = order.earlyBetAmount
            }
        }
        earlySettleStatus(order.earlySettlePrice.settleStatus)
    }

   /**
    *当提前结算单在提前结算中时提前结算按钮显示为提前结算中
    * */
    private fun earlySettleStatus(settleStatus:Int){
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