package arch.cayenne.module.betslip.ui.viewholder

import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.common.data.constants.CurrencySymbols
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
            mBinding.ivCopyClip.clickNoRepeat {
                mBetSlipListener?.onCopyClip(item.betId)
            }
        }
    }

    private fun sendData(item: BetSlipOrderBean) {
        submitItemData(item.selectionsList)
        setGradientLayout(mBinding.ilMore, mBinding.recyclerSelection)
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
            tvUnit2Value.text = item.odds.getDisplayOdds()
            val betAmount = "${CurrencySymbols.getSymbol(item.currency)}${item.betAmount.toMoney().getFormalMoney()}"
            tvUnit3Value.text = betAmount
            val exceptAmount = "${CurrencySymbols.getSymbol(item.currency)}${BetSlipUtils.expectMaxAmount(item.betAmount, item.odds)}"
            tvUnit4Value.text = exceptAmount
            tvUnit4.setTextRes(if(item.comboType == 0) R.string.live_bet_except_win else R.string.live_bet_except_max_win)
        }
    }
}