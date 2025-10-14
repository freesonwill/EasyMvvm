package arch.cayenne.module.order.ui.compare

import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.module.order.data.model.ShareBean

class ShareBeanCompare : DiffUtil.ItemCallback<ShareBean>() {
    override fun areItemsTheSame(
        oldItem: ShareBean,
        newItem: ShareBean
    ): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(
        oldItem: ShareBean,
        newItem: ShareBean
    ): Boolean {
        return oldItem == newItem
    }
}