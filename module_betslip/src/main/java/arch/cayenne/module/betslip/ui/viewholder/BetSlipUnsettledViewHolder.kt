package arch.cayenne.module.betslip.ui.viewholder

import androidx.core.view.isVisible
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.common.data.constants.CurrencySymbols
import arch.cayenne.lib.common.ui.adapter.RecyclerItemListener
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.SportDisplayOddsExt.getDisplayOdds
import arch.cayenne.lib.common.utils.ext.SportIntExt.getFormalMoney
import arch.cayenne.lib.common.utils.ext.SportStringExt.toMoney
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.getDetailFormatDate
import arch.cayenne.lib.database.entity.BetSlipData
import arch.cayenne.lib.database.entity.BetSlipOrderBean
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
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
            mBinding.ivCopyClip.clickNoRepeat {
                mBetSlipListener?.onCopyClip(item.betId)
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
            val settlePrice = BetSlipUtils.earlySettlePrice(
                order.betAmount, order.earlyBetAmount, order.earlySettlePrice.price
            )
            val isCanSettle = settlePrice.toMoney() > 1000 && order.earlySupport
            it.betUnsettledBtSettle.isEnabled = isCanSettle
            it.betUnsettledBtSettle.isVisible = isCanSettle
            it.betUnsettledBtSettle.tag = adapterPosition
            it.betUnsettledTvBetcodeValue.text = order.betId
            val odds = "@${order.odds.getDisplayOdds()}"
            it.betUnsettledTvOddsValue.text = odds
            val betAmount = "${CurrencySymbols.getSymbol(order.currency)}${(order.betAmount - order.earlyBetAmount).getFormalMoney()}"
            it.betUnsettledTvBettingValue.text = betAmount
            val exceptAmount = "${CurrencySymbols.getSymbol(order.currency)}${BetSlipUtils.expectMaxAmount(order.betAmount, order.odds)}"
            it.betUnsettledTvExceptValue.text = exceptAmount
            it.betUnsettledTvExcept.setTextRes(if(order.comboType == 0) R.string.live_bet_except_win else R.string.live_bet_except_max_win)
            val earlyAmountStr = "${CurrencySymbols.getSymbol(order.currency)}${settlePrice}"
            it.betUnsettledBtAmount.text = earlyAmountStr
            val flag = order.comboType != 0  // 0 - 单关 1-串关 2-全窜关
            it.groupCrossborder.isVisible = flag

            if (flag) {
                val combo = "${R.string.title_combo_bet_odds.getString(order.comboK, order.comboV)}*${order.comboCount}"
                it.betUnsettledTvCrossborderValue.text = combo
            }
            it.groupEarlysettle.isVisible = order.earlyBetAmount > 0 //提前结算部分有金额才显示
            it.betUnsettledTvEarlysettleValue.text = order.earlyBetAmount.getFormalMoney()
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
            when (settleStatus) {
                102 -> {
                    it.betUnsettledBtProgress.isVisible = true
                    it.betUnsettledBtSettle.isEnabled = false
                }
                1000 -> {
                    it.betUnsettledBtProgress.isVisible = true
                    it.betUnsettledBtSettle.isEnabled = false
                }
                else -> {
                    it.betUnsettledBtProgress.isVisible = false
                }
            }
        }
    }

    fun setEarlySettleSubmitListener(listener: RecyclerItemListener<String>) {
        this.earlySettleSubmitListener = listener
    }
}