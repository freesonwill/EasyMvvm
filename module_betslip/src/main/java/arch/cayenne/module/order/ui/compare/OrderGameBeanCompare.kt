package arch.cayenne.module.order.ui.compare

import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.module.order.data.model.RecordsBean

class OrderGameBeanCompare : DiffUtil.ItemCallback<RecordsBean>() {

    override fun areItemsTheSame(
        oldItem: RecordsBean,
        newItem: RecordsBean
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: RecordsBean,
        newItem: RecordsBean
    ): Boolean {
        return oldItem == newItem
    }
}