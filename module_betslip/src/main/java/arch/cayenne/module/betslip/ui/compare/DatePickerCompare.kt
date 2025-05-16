package arch.cayenne.module.betslip.ui.compare

import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.module.betslip.data.model.DatePickerBean

class DatePickerCompare: DiffUtil.ItemCallback<DatePickerBean>() {
    override fun areItemsTheSame(oldItem: DatePickerBean, newItem: DatePickerBean): Boolean {
        return oldItem.date == newItem.date && oldItem.isSelected == newItem.isSelected
    }

    override fun areContentsTheSame(oldItem: DatePickerBean, newItem: DatePickerBean): Boolean {
        return oldItem == newItem
    }
}