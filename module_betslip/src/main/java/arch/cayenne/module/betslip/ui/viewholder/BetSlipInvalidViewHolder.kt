package arch.cayenne.module.betslip.ui.viewholder

import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.common.utils.ext.getDetailFormatDate
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.data.model.BetSlipData
import arch.cayenne.module.betslip.data.model.BetSlipOrder
import arch.cayenne.module.betslip.data.model.OrderBean
import arch.cayenne.module.betslip.databinding.AdapterLiveBetSlipInvalidBinding
import arch.cayenne.module.betslip.utisl.BetSlipUtils

class BetSlipInvalidViewHolder(binding: ViewBinding, private val betSlipType: BetSlipEnum) :
    BaseBetSlipViewHolder<AdapterLiveBetSlipInvalidBinding>(binding) {
    override fun createViewHolder() {
        initItemView(mBinding.recyclerSelection, betSlipType)
    }

    override fun covertPlus(item: BetSlipData) {
        if (item is BetSlipOrder) {
            updateData(item.order)
            submitOrderData(item)
        }
    }

    private fun updateData(
        item: OrderBean
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
            tvUnit3Value.text = item.betAmount
            tvUnit4Value.text = BetSlipUtils.expectMaxAmount(item.betAmount, item.odds)
        }
    }
}