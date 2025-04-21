package arch.cayenne.module.handicap.compare

import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.module.handicap.data.BigSmallBean

class BigSmallCompare : DiffUtil.ItemCallback<BigSmallBean>() {

    override fun areItemsTheSame(oldItem: BigSmallBean, newItem: BigSmallBean): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: BigSmallBean, newItem: BigSmallBean): Boolean {
        return oldItem == newItem
    }
}