package arch.cayenne.module.order.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.database.entity.BetSlipSelectionData
import arch.cayenne.lib.database.entity.OrderSelectionBean
import arch.cayenne.lib.database.entity.ReserveOrderSelectionBean
import arch.cayenne.module.betslip.databinding.ItemOrderSportBettingSelectionBinding
import arch.cayenne.module.betslip.ui.compare.BetSlipSelectionCompare
import arch.cayenne.module.order.data.constants.OrderSportPageEnum
import arch.cayenne.module.order.ui.viewholder.OrderBettingReserveSelectionViewHolder
import arch.cayenne.module.order.ui.viewholder.OrderBettingSelectionViewHolder

class OrderBettingSelectionAdapter(private val type: OrderSportPageEnum?, private val selectionListener: OrderBettingAdapter.SelectionItemListener?): BaseAdapter<BetSlipSelectionData, BaseViewHolder, ItemOrderSportBettingSelectionBinding>(
    BetSlipSelectionCompare()
) {
    override fun convertPlus(
        holder: BaseViewHolder,
        binding: ItemOrderSportBettingSelectionBinding,
        position: Int
    ) {
        when (holder) {
            is OrderBettingReserveSelectionViewHolder -> {
                val item = getItem(position) as ReserveOrderSelectionBean
                holder.init(item, itemCount == 1)
                holder.hideLine(position == 0)
            }
            is OrderBettingSelectionViewHolder -> {
                val item = getItem(position) as OrderSelectionBean
                holder.init(item, itemCount == 1)
                holder.hideLine(position == 0)
            }
        }
        binding.root.setOnClickListener {
            selectionListener?.onSingleClick(getItem(position))
        }
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
    ): BaseViewHolder {
        return if (type == OrderSportPageEnum.RESERVE) {
            OrderBettingReserveSelectionViewHolder(binding)
        } else {
            OrderBettingSelectionViewHolder(binding)
        }
    }
}