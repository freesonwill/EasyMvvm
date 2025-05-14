package arch.cayenne.module.picker

import androidx.recyclerview.widget.DiffUtil

class DatePickerCompare: DiffUtil.ItemCallback<DatePickerBean>() {
    override fun areItemsTheSame(oldItem: DatePickerBean, newItem: DatePickerBean): Boolean {
        return oldItem.date == newItem.date && oldItem.isSelected == newItem.isSelected
    }

    override fun areContentsTheSame(oldItem: DatePickerBean, newItem: DatePickerBean): Boolean {
        return oldItem == newItem
    }
}