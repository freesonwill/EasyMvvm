package arch.cayenne.module.betslip.ui.viewholder

import androidx.core.view.isVisible
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.getDetailFormatDate
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.data.constants.BetSlipExpandedEnum
import arch.cayenne.module.betslip.data.model.BetSlipData
import arch.cayenne.module.betslip.data.model.BetSlipOrderBean
import arch.cayenne.module.betslip.databinding.AdapterLiveBetSlipConfirmBinding
import arch.cayenne.module.betslip.utisl.BetSlipUtils

class BetSlipConfirmViewHolder(binding: ViewBinding, betSlipType: BetSlipEnum) :
    BaseBetSlipViewHolder<AdapterLiveBetSlipConfirmBinding>(binding, betSlipType) {

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
        item: BetSlipOrderBean
    ) {
        mBinding.also {
            item.let { order ->
                it.betConfirmTvDate.text = order.betTime.getDetailFormatDate()
                it.betConfirmTvBetcodeValue.text = order.betId
                it.betConfirmTvOddsValue.text = order.odds
                val betAmount = "${moneySymbol}${order.betAmount}"
                it.betConfirmTvBettingValue.text = betAmount
                val exceptAmount = "${moneySymbol}${BetSlipUtils.expectMaxAmount(order.betAmount, order.odds)}"
                it.betConfirmTvExceptValue.text = exceptAmount
            }
        }
    }
}