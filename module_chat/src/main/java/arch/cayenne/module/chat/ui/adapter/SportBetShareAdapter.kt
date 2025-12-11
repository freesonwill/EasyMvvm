package arch.cayenne.module.chat.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.database.entity.BetSlipData
import arch.cayenne.lib.database.entity.BetSlipOrderBean
import arch.cayenne.module.betslip.ui.compare.BetSlipCompare
import arch.cayenne.module.chat.databinding.ItemShareBetLayoutBinding
import arch.cayenne.module.chat.ui.adapter.viewholder.SportShareBetViewHolder
import arch.cayenne.module.order.data.constants.OrderSportPageEnum

/**
 * @author: wenxi
 * @date: 10/12/25 16:15
 * @description:
 */
class SportBetShareAdapter :
    BaseAdapter<BetSlipData, SportShareBetViewHolder, ItemShareBetLayoutBinding>(BetSlipCompare()) {
    override fun convertPlus(
        holder: SportShareBetViewHolder,
        binding: ItemShareBetLayoutBinding,
        position: Int
    ) {
        val item = getItem(position) as BetSlipOrderBean
        holder.init(item, OrderSportPageEnum.UNSETTLED)
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemShareBetLayoutBinding {
        return ItemShareBetLayoutBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(
        binding: ItemShareBetLayoutBinding,
        viewType: Int
    ): SportShareBetViewHolder {
        return SportShareBetViewHolder(binding)
    }
}