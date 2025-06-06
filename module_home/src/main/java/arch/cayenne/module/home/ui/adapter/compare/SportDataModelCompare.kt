package arch.cayenne.module.home.ui.adapter.compare

import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.lib.database.entity.SportDataModel

class SportDataModelCompare: DiffUtil.ItemCallback<SportDataModel>() {
    override fun areItemsTheSame(oldItem: SportDataModel, newItem: SportDataModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: SportDataModel, newItem: SportDataModel): Boolean {
        return oldItem == newItem
    }
}