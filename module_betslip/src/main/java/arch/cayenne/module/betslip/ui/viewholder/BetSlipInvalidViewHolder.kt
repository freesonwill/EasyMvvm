package arch.cayenne.module.betslip.ui.viewholder

import androidx.core.content.ContextCompat
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
import arch.cayenne.module.betslip.databinding.AdapterLiveBetSlipInvalidBinding
import arch.cayenne.module.betslip.utisl.BetSlipUtils

class BetSlipInvalidViewHolder(binding: ViewBinding, betSlipType: BetSlipEnum) :
    BaseBetSlipViewHolder<AdapterLiveBetSlipInvalidBinding>(binding, betSlipType) {
    override fun createViewHolder() {
        initItemView(mBinding.recyclerSelection)
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
        setGradientLayout(mBinding.ilMore)
    }

    private fun updateData(
        item: BetSlipOrderBean
    ) {
        with(mBinding) {
            betExpiredTvDate.text = item.betTime.getDetailFormatDate()
            betExpiredTvStatus.text = root.context.resources.getString(R.string.live_bet_rejection)
            betExpiredTvStatus.setBackgroundResource(SkinnableResourceManager.getTargetResourceId(root.context, R.drawable.bg_rejection))
            tvUnit1.text = ContextCompat.getString(binding.root.context, R.string.live_bet_bet_num)
            tvUnit2.text = ContextCompat.getString(binding.root.context, R.string.live_bet_odds)
            tvUnit3.text = ContextCompat.getString(binding.root.context, R.string.live_bet_on)
            tvUnit4.text = ContextCompat.getString(binding.root.context, R.string.live_bet_except_max_win)
            tvUnit1Value.text = item.betId
            tvUnit2Value.text = item.odds
            val betAmount = "${moneySymbol}${item.betAmount}"
            tvUnit3Value.text = betAmount
            val exceptAmount = "${moneySymbol}${BetSlipUtils.expectMaxAmount(item.betAmount, item.odds)}"
            tvUnit4Value.text = exceptAmount
        }
    }
}