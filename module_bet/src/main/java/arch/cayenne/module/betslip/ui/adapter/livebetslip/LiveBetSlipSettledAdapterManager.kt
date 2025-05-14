package arch.cayenne.module.betslip.ui.adapter.livebetslip

import androidx.core.content.ContextCompat
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.bet.databinding.AdapterLiveBetSlipSettledBinding
import arch.cayenne.module.betslip.data.constants.LiveBetSlipEnum
import arch.cayenne.module.betslip.data.constants.LiveBetSlipResultOrderStatusEnum
import arch.cayenne.module.betslip.data.model.LiveBetSlipData
import arch.cayenne.module.betslip.utisl.LiveBetSlipUtils.expectMaxAmount
import galaxy.common.proto.Common.Order

class LiveBetSlipSettledAdapterManager(
    private val binding: AdapterLiveBetSlipSettledBinding,
    private val betSlipType: LiveBetSlipEnum
) : LiveBetSlipBaseAdapterManager(binding, betSlipType) {

    override fun createViewHolder() {
        initRecyclerView(binding.recyclerSelection, betSlipType)
        binding.ivTip.clickNoRepeat {
            showBetTip(binding.ivTip)
        }
    }

    override fun covertPlus(position: Int, item: LiveBetSlipData) {
        item.order?.let {
            updateData(it)
            submitAdapter(binding.recyclerSelection, item, position)
        }
    }

    /**
     *未结算 确认中 已结算 更新数据
     */
    private fun updateData(order: Order) {
        binding.also {
            it.betSettledTvBetcodeValue.text = order.betId
            it.betSettledTvOddsValue.text = order.odds
            it.betSettledTvBettingValue.text = order.betAmount
            it.betSettledTvExceptValue.text = expectMaxAmount(order.betAmount, order.odds)
            settledStatus(order)
        }
    }

    private fun settledStatus(item: Order) {
        binding.also {
            val status = LiveBetSlipResultOrderStatusEnum.getStatus(item.resultStatus)
            status?.let { st ->
                it.betSettledTvResult.text =  ContextCompat.getString(it.betSettledTvResult.context,st.names)
                it.betSettledTvResult.background =
                    SkinnableResourceManager.getDrawable(it.betSettledTvResult.context, st.resId)
            }
        }
    }

}