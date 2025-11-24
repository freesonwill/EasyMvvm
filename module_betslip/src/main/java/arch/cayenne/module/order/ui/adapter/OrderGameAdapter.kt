package arch.cayenne.module.order.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.module.betslip.databinding.ItemOrderGameBinding
import arch.cayenne.module.order.data.model.RecordsBean
import arch.cayenne.module.order.ui.compare.OrderGameBeanCompare
import arch.cayenne.module.order.ui.viewholder.OrderGameViewHolder

class OrderGameAdapter :
    BaseAdapter<RecordsBean, OrderGameViewHolder, ItemOrderGameBinding>(
        OrderGameBeanCompare()
    ) {
    override fun convertPlus(
        holder: OrderGameViewHolder,
        binding: ItemOrderGameBinding,
        position: Int
    ) {
        val item = getItem(position)
        holder.init(item)
        holder.hideLine(position, itemCount)
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemOrderGameBinding {
        return ItemOrderGameBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(
        binding: ItemOrderGameBinding,
        viewType: Int
    ): OrderGameViewHolder {
        return OrderGameViewHolder(binding)
    }
}