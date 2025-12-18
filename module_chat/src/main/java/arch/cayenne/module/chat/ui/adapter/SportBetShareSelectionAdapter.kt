package arch.cayenne.module.chat.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.database.entity.BetSlipSelectionData
import arch.cayenne.lib.database.entity.OrderSelectionBean
import arch.cayenne.module.betslip.ui.compare.BetSlipSelectionCompare
import arch.cayenne.module.chat.databinding.ItemShareBetSelectionLayoutBinding
import arch.cayenne.module.chat.ui.adapter.viewholder.SportShareBetSelectionViewHolder
import arch.cayenne.module.order.data.constants.OrderSportPageEnum

/**
 * @author: wenxi
 * @date: 11/12/25 10:47
 * @description:
 */
class SportBetShareSelectionAdapter(private val type: OrderSportPageEnum): BaseAdapter<BetSlipSelectionData, SportShareBetSelectionViewHolder, ItemShareBetSelectionLayoutBinding>(
    BetSlipSelectionCompare()
) {
    override fun convertPlus(
        holder: SportShareBetSelectionViewHolder,
        binding: ItemShareBetSelectionLayoutBinding,
        position: Int
    ) {
        val item = getItem(position) as OrderSelectionBean
        holder.init(item, itemCount == 1)

        holder.hideLine(position == 0)
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemShareBetSelectionLayoutBinding {
        return ItemShareBetSelectionLayoutBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(
        binding: ItemShareBetSelectionLayoutBinding,
        viewType: Int
    ): SportShareBetSelectionViewHolder {
        return SportShareBetSelectionViewHolder(binding)
    }
}