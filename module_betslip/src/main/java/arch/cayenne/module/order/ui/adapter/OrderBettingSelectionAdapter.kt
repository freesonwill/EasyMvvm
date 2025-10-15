package arch.cayenne.module.order.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.database.entity.BetSlipSelectionData
import arch.cayenne.lib.database.entity.OrderSelectionBean
import arch.cayenne.module.betslip.databinding.ItemOrderSportBettingSelectionBinding
import arch.cayenne.module.betslip.ui.compare.BetSlipSelectionCompare
import arch.cayenne.module.order.data.constants.OrderSportPageEnum
import arch.cayenne.module.order.ui.viewholder.OrderBettingSelectionViewHolder

class OrderBettingSelectionAdapter(private val type: OrderSportPageEnum): BaseAdapter<BetSlipSelectionData, OrderBettingSelectionViewHolder, ItemOrderSportBettingSelectionBinding>(
    BetSlipSelectionCompare()
) {
    override fun convertPlus(
        holder: OrderBettingSelectionViewHolder,
        binding: ItemOrderSportBettingSelectionBinding,
        position: Int
    ) {
        val item = getItem(position) as OrderSelectionBean
        holder.init(item)
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemOrderSportBettingSelectionBinding {
        return ItemOrderSportBettingSelectionBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(
        binding: ItemOrderSportBettingSelectionBinding,
        viewType: Int
    ): OrderBettingSelectionViewHolder {
        return OrderBettingSelectionViewHolder(binding)
    }
}