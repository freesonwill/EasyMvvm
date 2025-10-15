package arch.cayenne.module.order.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.SportIntExt.getOdds
import arch.cayenne.lib.database.entity.BetSlipData
import arch.cayenne.lib.database.entity.BetSlipOrderBean
import arch.cayenne.module.betslip.databinding.ItemOrderSportBettingBinding
import arch.cayenne.module.betslip.ui.compare.BetSlipCompare
import arch.cayenne.module.order.data.constants.OrderSportPageEnum

class OrderBettingAdapter(private val type: OrderSportPageEnum): BaseAdapter<BetSlipData, BaseViewHolder, ItemOrderSportBettingBinding>(
    BetSlipCompare()
) {
    override fun convertPlus(
        holder: BaseViewHolder,
        binding: ItemOrderSportBettingBinding,
        position: Int
    ) {
        val item = getItem(position) as BetSlipOrderBean
        val selectionAdapter = OrderBettingSelectionAdapter(type)
        binding.rvContent.adapter = selectionAdapter

        binding.tvBetOdds.text = item.odds.getOdds()
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemOrderSportBettingBinding {
        return ItemOrderSportBettingBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(
        binding: ItemOrderSportBettingBinding,
        viewType: Int
    ): BaseViewHolder {
        return getBaseViewHolder(binding)
    }
}