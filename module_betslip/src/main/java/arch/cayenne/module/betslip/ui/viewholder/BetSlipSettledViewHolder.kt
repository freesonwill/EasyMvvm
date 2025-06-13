package arch.cayenne.module.betslip.ui.viewholder

import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.getDetailFormatDate
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.data.constants.BetSlipResultOrderStatusEnum
import arch.cayenne.module.betslip.data.model.BetSlipData
import arch.cayenne.module.betslip.data.model.BetSlipOrder
import arch.cayenne.module.betslip.data.model.OrderBean
import arch.cayenne.module.betslip.databinding.AdapterLiveBetSlipSettledBinding
import arch.cayenne.module.betslip.utisl.BetSlipUtils

class BetSlipSettledViewHolder(binding: ViewBinding, private val betSlipType: BetSlipEnum) :
    BaseBetSlipViewHolder<AdapterLiveBetSlipSettledBinding>(binding) {

    override fun createViewHolder() {
        initItemView(mBinding.recyclerSelection, betSlipType)
        mBinding.ivTip.clickNoRepeat {
            showBetTip(mBinding.ivTip)
        }
    }

    override fun covertPlus(item: BetSlipData) {
        if (item is BetSlipOrder) {
            item.order.let {
                updateData(it)
                submitOrderData(item)
            }
        }
    }

    /**
     *未结算 确认中 已结算 更新数据
     */
    private fun updateData(order: OrderBean) {
        mBinding.also {
            it.betSettledTvDate.text = order.betTime.getDetailFormatDate()
            it.betSettledTvBetcodeValue.text = order.betId
            it.betSettledTvOddsValue.text = order.odds
            it.betSettledTvBettingValue.text = order.betAmount
            val amount = BetSlipUtils.winOrLoseAmount(
                order.betAmount,
                order.earlyBetAmount,
                order.returnAmount
            )
            settledStatus(order)
            it.betSettledTvExceptValue.text = "$amount"
            it.betSettledTvExceptValue.setTextColor(
                ContextCompat.getColorStateList(
                    binding.root.context,
                    if (amount >= 0) arch.cayenne.lib.res.R.color.win_color else arch.cayenne.lib.res.R.color.lose_color
                )
            )
        }
    }

    private fun settledStatus(item: OrderBean) {
        mBinding.also {
            val status = BetSlipResultOrderStatusEnum.getStatus(item.resultStatus)
            status?.let { st ->
                it.betSettledTvResult.text =  ContextCompat.getString(it.betSettledTvResult.context,st.names)
                it.betSettledTvResult.background = SkinnableResourceManager.getDrawable(it.betSettledTvResult.context,st.resId)
            }
        }
    }
}