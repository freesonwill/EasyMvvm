package arch.cayenne.module.betslip.ui.viewholder

import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.common.data.constants.CurrencySymbols
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.SportDisplayOddsExt.getDisplayOdds
import arch.cayenne.lib.common.utils.ext.SportIntExt.getFormalMoney
import arch.cayenne.lib.common.utils.ext.SportStringExt.toMoney
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.getDetailFormatDate
import arch.cayenne.lib.database.entity.BetSlipData
import arch.cayenne.lib.database.entity.BetSlipOrderBean
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.data.constants.BetSlipResultOrderStatusEnum
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
     *未结算 确认中 已结算 更新数据
     */
    private fun updateData(order: BetSlipOrderBean) {
        mBinding.also {
            it.betSettledTvDate.text = order.betTime.getDetailFormatDate()
            it.betSettledTvBetcodeValue.text = order.betId
            it.betSettledTvOddsValue.text = order.odds.getDisplayOdds()
            val betAmount = "${CurrencySymbols.getSymbol(order.currency)}${order.betAmount.getFormalMoney()}"
            it.betSettledTvBettingValue.text = betAmount
            settledStatus(order)

            val hasPartSettled =
                BetSlipResultOrderStatusEnum.getStatus(order.resultStatus) == BetSlipResultOrderStatusEnum.EarlySettle
            val earlyAmount = "${CurrencySymbols.getSymbol(order.currency)}${order.earlyBetAmount.getFormalMoney()}"
            mBinding.betSettledTvPartValue.text = earlyAmount
            val amount =
                (if (hasPartSettled) order.earlyReturnAmount else order.returnAmount)
            it.betSettledTvExceptValue.text =
                if (amount >= 0) "${CurrencySymbols.getSymbol(order.currency)}${amount.getFormalMoney()}" else "-${CurrencySymbols.getSymbol(order.currency)}${amount.getFormalMoney()}"

            val flag = order.comboType != 0  // 0 - 单关 1-串关 2-全窜关
            it.groupCrossborder.isVisible = flag
            if (flag) {
                val combo = R.string.title_combo_bet_odds.getString(order.comboK, order.comboV)
                val comboValue = combo //"$combo*${order.comboCount}" 修改为类似 2串1
                it.betSettledTvCrossborderValue.text = comboValue
            }

            //按照h5串关不支持提前计算，串关时不限时部分提前结算
            mBinding.betSettledTvPart.isVisible = !flag
            mBinding.betSettledTvPartValue.isVisible = !flag

            val colorRes = if (amount > 0) {
                arch.cayenne.lib.res.R.color.win_color
            } else if (amount < 0) {
                arch.cayenne.lib.res.R.color.lose_color
            } else {
                null
            }
            colorRes?.let { color ->
                it.betSettledTvExceptValue.setTextColor(
                    ContextCompat.getColorStateList(
                        binding.root.context,
                        color
                    )
                )
            }
        }
    }

    private fun settledStatus(item: BetSlipOrderBean) {
        mBinding.also {
            val status = BetSlipResultOrderStatusEnum.getStatus(item.resultStatus)
            status?.let { st ->
                it.betSettledTvResult.setTextRes(st.names)
                it.betSettledTvResult.background =
                    SkinnableResourceManager.getDrawable(it.betSettledTvResult.context, st.resId)
            }
        }
    }
}