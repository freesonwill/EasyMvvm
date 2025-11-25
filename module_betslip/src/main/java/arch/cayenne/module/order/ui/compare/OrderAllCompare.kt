package arch.cayenne.module.order.ui.compare

import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.module.order.data.model.OrderAllBean

class OrderAllCompare : DiffUtil.ItemCallback<OrderAllBean>() {

    override fun areItemsTheSame(
        oldItem: OrderAllBean,
        newItem: OrderAllBean
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: OrderAllBean,
        newItem: OrderAllBean
    ): Boolean {
        return oldItem == newItem
    }
}