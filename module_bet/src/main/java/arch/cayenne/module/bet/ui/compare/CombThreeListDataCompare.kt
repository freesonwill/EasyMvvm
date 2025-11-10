package arch.cayenne.module.bet.ui.compare

import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.module.bet.data.CombThreeListData

class CombThreeListDataCompare: DiffUtil.ItemCallback<CombThreeListData>() {
    override fun areItemsTheSame(
        oldItem: CombThreeListData,
        newItem: CombThreeListData
    ): Boolean {
        return oldItem.iid == newItem.iid
    }

    override fun areContentsTheSame(
        oldItem: CombThreeListData,
        newItem: CombThreeListData
    ): Boolean {
        return oldItem == newItem
    }
}