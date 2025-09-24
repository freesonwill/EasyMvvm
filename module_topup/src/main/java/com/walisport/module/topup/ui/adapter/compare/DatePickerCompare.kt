package com.walisport.module.topup.ui.adapter.compare

import androidx.recyclerview.widget.DiffUtil
import com.walisport.module.topup.data.DateFilterBean

class DatePickerCompare: DiffUtil.ItemCallback<DateFilterBean>() {

    override fun areItemsTheSame(oldItem: DateFilterBean, newItem: DateFilterBean): Boolean {
        return oldItem.title == newItem.title && oldItem.isSelected == newItem.isSelected
    }

    override fun areContentsTheSame(oldItem: DateFilterBean, newItem: DateFilterBean): Boolean {
        return oldItem == newItem
    }
}