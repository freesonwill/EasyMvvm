package arch.cayenne.module.betslip.ui.compare

import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.module.betslip.data.model.DateFilterBean

class DatePickerCompare: DiffUtil.ItemCallback<DateFilterBean>() {
    override fun areItemsTheSame(oldItem: DateFilterBean, newItem: DateFilterBean): Boolean {
        return oldItem.title == newItem.title && oldItem.isSelected == newItem.isSelected
    }

    override fun areContentsTheSame(oldItem: DateFilterBean, newItem: DateFilterBean): Boolean {
        return oldItem == newItem
    }
}