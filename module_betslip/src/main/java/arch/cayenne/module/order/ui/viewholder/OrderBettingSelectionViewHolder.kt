package arch.cayenne.module.order.ui.viewholder

import androidx.core.view.isVisible
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.SportIntExt.getOdds
import arch.cayenne.lib.database.entity.OrderSelectionBean
import arch.cayenne.module.betslip.databinding.ItemOrderSportBettingSelectionBinding

class OrderBettingSelectionViewHolder(private val mBinding: ItemOrderSportBettingSelectionBinding): BaseViewHolder(mBinding) {

    fun init(item: OrderSelectionBean) {
        mBinding.tvSelectionName.text = item.selectionName
        mBinding.tvStatus.isVisible = item.inPlay
        mBinding.tvMarket.text = item.marketName
        mBinding.tvOdds.text = item.odds.getOdds()

        mBinding.tvReserve.isVisible = false
    }
}