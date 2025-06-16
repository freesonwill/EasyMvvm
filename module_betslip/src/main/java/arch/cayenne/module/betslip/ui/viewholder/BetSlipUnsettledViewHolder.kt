package arch.cayenne.module.betslip.ui.viewholder

import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.common.ui.adapter.RecyclerItemListener
import arch.cayenne.lib.common.ui.view.ProgressDrawable
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.getDetailFormatDate
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.data.constants.BetSlipExpandedEnum
import arch.cayenne.module.betslip.data.model.BetSlipData
import arch.cayenne.module.betslip.data.model.BetSlipOrderBean
import arch.cayenne.module.betslip.databinding.AdapterLiveBetSlipUnsettleBinding
import arch.cayenne.module.betslip.utisl.BetSlipUtils

class BetSlipUnsettledViewHolder(binding: ViewBinding, betSlipType: BetSlipEnum):
    BaseBetSlipViewHolder<AdapterLiveBetSlipUnsettleBinding>(binding, betSlipType) {

    private var earlySettleSubmitListener: RecyclerItemListener<String>? = null

    override fun createViewHolder() {
        initItemView(mBinding.recyclerSelection)
        mBinding.ivTip.clickNoRepeat {
            showBetTip(it)
        }
    }

    override fun covertPlus(item: BetSlipData) {
        if (item is BetSlipOrderBean) {
            updateData(item)
            sendData(item)
            mBinding.ilMore.llMore.clickNoRepeat {
                sendData(item)
            }
        }
    }

    private fun sendData(item: BetSlipOrderBean) {
        submitItemData(item.selectionsList)
        setGradientLayout(mBinding.ilMore, mBinding.recyclerSelection)
    }

    /**
     * 未结算 确认中 已结算 更新数据
     * */
    private fun updateData(
        order: BetSlipOrderBean
    ) {
        mBinding.also {
            it.betUnsettledTvDate.text = order.betTime.getDetailFormatDate()

            // 支援提前結算 且 仍有可結算次數 且 可結算金額大於等於最小結算金額
            it.betUnsettledBtSettle.isEnabled =
                order.earlySupport && order.earlySettleTimes < order.earlySettlePrice.settleTotal && BetSlipUtils.calculateMinSettlePrice(
                    order.betAmount, order.earlyBetAmount, order.earlySettlePrice.settleMin
                )
            it.betUnsettledBtSettle.tag = adapterPosition
            it.betUnsettledTvBetcodeValue.text = order.id
            it.betUnsettledTvOddsValue.text = order.odds
            val betAmount = "${moneySymbol}${order.betAmount}"
            it.betUnsettledTvBettingValue.text = betAmount
            val exceptAmount = "${moneySymbol}${BetSlipUtils.expectMaxAmount(order.betAmount, order.odds)}"
            it.betUnsettledTvExceptValue.text = exceptAmount
            val earlyAmount = "${moneySymbol}${BetSlipUtils.earlySettlePrice(order.betAmount, order.earlyBetAmount)}"
            it.betUnsettledBtAmount.text = earlyAmount
            val flag = order.comboType != 0  // 0 - 单关 1-串关 2-全窜关
            it.groupCrossborder.isVisible = flag
            if (flag) {
                val combo = R.string.title_combo_bet_odds.getString(order.comboK, order.comboV)
                val comboValue = "$combo*${order.comboCount}"
                it.betUnsettledTvCrossborderValue.text = comboValue
            }
            it.groupEarlysettle.isVisible = order.earlySupport
            if (order.earlySupport) {
                it.betUnsettledTvEarlysettleValue.text = order.earlyBetAmount
            }
            it.betUnsettledBtSettle.clickNoRepeat {
                if (order.earlySettlePrice.settleStatus != 102) {
                    earlySettleSubmitListener?.onItemClick("", adapterPosition)
                }
            }
        }
        earlySettleStatus(order.earlySettlePrice.settleStatus)
    }

    /**
     *当提前结算单在提前结算中时提前结算按钮显示为提前结算中
     * */
    private fun earlySettleStatus(settleStatus: Int) {
        mBinding.also {
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

    fun setEarlySettleSubmitListener(listener: RecyclerItemListener<String>) {
        this.earlySettleSubmitListener = listener
    }
}