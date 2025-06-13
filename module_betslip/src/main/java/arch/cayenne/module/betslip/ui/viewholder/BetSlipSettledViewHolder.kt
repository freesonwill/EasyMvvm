package arch.cayenne.module.betslip.ui.viewholder

import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.common.utils.ext.SportIntExt.getMoney
import arch.cayenne.lib.common.utils.ext.SportStringExt.toMoney
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.getDetailFormatDate
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.data.constants.BetSlipResultOrderStatusEnum
import arch.cayenne.module.betslip.data.model.BetSlipData
import arch.cayenne.module.betslip.data.model.BetSlipOrderBean
import arch.cayenne.module.betslip.databinding.AdapterLiveBetSlipSettledBinding

class BetSlipSettledViewHolder(binding: ViewBinding, betSlipType: BetSlipEnum) :
    BaseBetSlipViewHolder<AdapterLiveBetSlipSettledBinding>(binding, betSlipType) {

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
            mBinding.ilMore.tvMore.clickNoRepeat {
                sendData(item)
            }
        }
    }

    private fun sendData(item: BetSlipOrderBean) {
        submitItemData(item.selectionsList)
        setGradientLayout(mBinding.ilMore)
    }

    /**
     *未结算 确认中 已结算 更新数据
     */
    private fun updateData(order: BetSlipOrderBean) {
        mBinding.also {
            it.betSettledTvDate.text = order.betTime.getDetailFormatDate()
            it.betSettledTvBetcodeValue.text = order.betId
            it.betSettledTvOddsValue.text = order.odds
            val betAmount = "${moneySymbol}${order.betAmount}"
            it.betSettledTvBettingValue.text = betAmount
            settledStatus(order)

            val hasPartSettled = BetSlipResultOrderStatusEnum.getStatus(order.resultStatus) == BetSlipResultOrderStatusEnum.EarlySettle
            mBinding.betSettledTvPart.isVisible = hasPartSettled
            mBinding.betSettledTvPartValue.isVisible = hasPartSettled
            val earlyAmount = "${moneySymbol}${order.earlyBetAmount}"
            mBinding.betSettledTvPartValue.text = earlyAmount

            val amount = (if (hasPartSettled) order.earlyReturnAmount else order.returnAmount).toMoney()
            it.betSettledTvExceptValue.text = if (amount >= 0) "${moneySymbol}${amount.getMoney()}" else "-${moneySymbol}${amount.getMoney()}"
            it.betSettledTvExceptValue.setTextColor(
                ContextCompat.getColorStateList(
                    binding.root.context,
                    if (amount >= 0) arch.cayenne.lib.res.R.color.win_color else arch.cayenne.lib.res.R.color.lose_color
                )
            )
        }
    }

    private fun settledStatus(item: BetSlipOrderBean) {
        mBinding.also {
            val status = BetSlipResultOrderStatusEnum.getStatus(item.resultStatus)
            status?.let { st ->
                it.betSettledTvResult.text =  ContextCompat.getString(it.betSettledTvResult.context,st.names)
                it.betSettledTvResult.background = SkinnableResourceManager.getDrawable(it.betSettledTvResult.context,st.resId)
            }
        }
    }
}