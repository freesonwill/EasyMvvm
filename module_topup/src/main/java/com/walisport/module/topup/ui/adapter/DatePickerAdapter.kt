package com.walisport.module.topup.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import com.walisport.module.topup.data.DateFilterBean
import com.walisport.module.topup.databinding.ItemDateBinding
import com.walisport.module.topup.ui.adapter.compare.DatePickerCompare
import com.walisport.module.topup.ui.viewholder.DatePickerViewHolder

class DatePickerAdapter(private val listener: OnDateClickListener): BaseAdapter<DateFilterBean, DatePickerViewHolder, ItemDateBinding>(
    DatePickerCompare()
) {
    override fun convertPlus(holder: DatePickerViewHolder, binding: ItemDateBinding, position: Int) {
        val bean = getItem(position)
        holder.init(bean, listener, itemCount)
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemDateBinding {
        return ItemDateBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(binding: ItemDateBinding, viewType: Int): DatePickerViewHolder {
        return DatePickerViewHolder(binding)
    }

    interface OnDateClickListener {
        fun onCustomClick()
        fun onDateClick(position: Int)
        fun onCancelClick()
    }
}