package arch.cayenne.module.handicap.compare

import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.module.handicap.data.LetBallBean

class LetBallCompare : DiffUtil.ItemCallback<LetBallBean>() {

    override fun areItemsTheSame(oldItem: LetBallBean, newItem: LetBallBean): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: LetBallBean, newItem: LetBallBean): Boolean {
        return oldItem == newItem
    }
}