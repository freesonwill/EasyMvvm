package arch.cayenne.lib.common.ui.compare

import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.lib.common.data.constants.ShareBean

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