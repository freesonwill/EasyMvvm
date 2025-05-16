package arch.cayenne.module.betslip.ui.adapter.livebetslip

import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import galaxy.common.proto.Common.Order
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.databinding.AdapterLiveBetSlipInvalidBinding
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.utisl.BetSlipUtils.expectMaxAmount

class BetSlipInvalidAdapterManager(
    private val binding: AdapterLiveBetSlipInvalidBinding, private val betSlipType: BetSlipEnum
) : BetSlipBaseAdapterManager(binding, betSlipType) {

    override fun createViewHolder() {
        initRecyclerView(binding.recyclerSelection,betSlipType)
    }

    override fun covertPlus(position: Int, item: arch.cayenne.module.betslip.data.model.BetSlipData) {
        item.order?.let {
            updateData(item.order, false)
        }
        submitAdapter(binding.recyclerSelection,item,position)
    }

    /**
     * 失效更新数据
     * */
    private fun updateData(
        item: Order, isReserve: Boolean
    ) {
        if (isReserve) {
            updateReserve(item)
        } else {
            updateInvalid(item)
        }
    }

    private fun updateReserve(item: Order) {
        with(binding) {
            betExpiredTvStatus.text =
                root.context.resources.getString(R.string.live_bet_reserve_expired)
            betExpiredTvStatus.setBackgroundResource(
                SkinnableResourceManager.getTargetResourceId(
                    root.context, R.drawable.bg_reser_expired
                )
            )
            tvUnit1.text =
                ContextCompat.getString(binding.root.context, R.string.live_bet_reserve_odds)
            tvUnit2.text =
                ContextCompat.getString(binding.root.context, R.string.live_bet_reserve_bet)
            tvUnit3.text =
                ContextCompat.getString(binding.root.context, R.string.live_bet_except_max_win)
            tvUnit4.isVisible = false
            tvUnit4Value.isVisible = false

            tvUnit1Value.text = item.betId //预约赔率
            tvUnit2Value.text = item.odds  //预约投注
            tvUnit3Value.text = item.betAmount //预约最高可赢
        }
    }

    private fun updateInvalid(item: Order) {
        with(binding) {
            betExpiredTvStatus.text = root.context.resources.getString(R.string.live_bet_rejection)
            betExpiredTvStatus.setBackgroundResource(SkinnableResourceManager.getTargetResourceId(root.context, R.drawable.bg_rejection))
            tvUnit1.text = ContextCompat.getString(binding.root.context, R.string.live_bet_bet_num)
            tvUnit2.text = ContextCompat.getString(binding.root.context, R.string.live_bet_odds)
            tvUnit3.text = ContextCompat.getString(binding.root.context, R.string.live_bet_on)
            tvUnit4.text = ContextCompat.getString(binding.root.context, R.string.live_bet_except_max_win)
            tvUnit1Value.text = item.betId
            tvUnit2Value.text = item.odds
            tvUnit3Value.text = item.betAmount
            tvUnit4Value.text = expectMaxAmount(item.betAmount, item.odds)
        }
    }
}