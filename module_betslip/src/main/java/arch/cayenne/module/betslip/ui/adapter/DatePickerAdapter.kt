package arch.cayenne.module.betslip.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.module.betslip.data.model.DateFilterBean
import arch.cayenne.module.betslip.databinding.ItemDateBinding
import arch.cayenne.module.betslip.ui.compare.DatePickerCompare
import arch.cayenne.module.betslip.ui.viewholder.DatePickerViewHolder

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