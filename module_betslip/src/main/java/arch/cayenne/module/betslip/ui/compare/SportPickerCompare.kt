package arch.cayenne.module.betslip.ui.compare

import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.module.betslip.data.model.SportFilterBean

class SportPickerCompare: DiffUtil.ItemCallback<SportFilterBean>() {
    override fun areItemsTheSame(oldItem: SportFilterBean, newItem: SportFilterBean): Boolean {
        return oldItem.sportId == newItem.sportId && oldItem.isSelected == newItem.isSelected
    }

    override fun areContentsTheSame(oldItem: SportFilterBean, newItem: SportFilterBean): Boolean {
        return oldItem == newItem
    }
}