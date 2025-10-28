package arch.cayenne.module.order.ui.viewholder

import androidx.core.view.isVisible
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.SportDisplayOddsExt.getDisplayOdds
import arch.cayenne.lib.database.entity.OrderSelectionBean
import arch.cayenne.module.betslip.databinding.ItemOrderSportBettingSelectionBinding

class OrderBettingSelectionViewHolder(private val mBinding: ItemOrderSportBettingSelectionBinding): BaseViewHolder(mBinding) {

    fun init(item: OrderSelectionBean) {
        mBinding.tvSelectionName.text = item.selectionName
        mBinding.tvStatus.isVisible = item.inPlay
        mBinding.tvMarket.text = item.marketName
        mBinding.tvOdds.text = item.odds.getDisplayOdds()

        mBinding.tvReserve.isVisible = false

        val isLive = isShowLiveButton(item.matchBasic.status)
        mBinding.ivLive.isVisible = isLive
        mBinding.tvLive.isVisible = isLive
    }

    private fun isShowLiveButton(status: Int): Boolean {
        return status == 1 ||
                status == 4 ||
                status == 5 ||
                status == 6 ||
                status == 8
    }

    fun hideLastLine(isLast:Boolean) {
        mBinding.line.isVisible = !isLast
    }
}