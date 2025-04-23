package arch.cayenne.module.handicap.compare

import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.module.handicap.data.CornerBallBean

class CornerBallCompare : DiffUtil.ItemCallback<CornerBallBean>() {

    override fun areItemsTheSame(oldItem: CornerBallBean, newItem: CornerBallBean): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: CornerBallBean, newItem: CornerBallBean): Boolean {
        return oldItem == newItem
    }
}