package arch.cayenne.module.picker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.module.bet.databinding.ItemDateBinding

class DatePickerAdapter(private val listener: OnDateClickListener): BaseAdapter<DatePickerBean, BaseViewHolder, ItemDateBinding>(
    DatePickerCompare()
) {
    override fun convertPlus(holder: BaseViewHolder, binding: ItemDateBinding, position: Int) {
       val bean = getItem(position)
        binding.tvTitle.text = bean.date
        binding.ivCancel.isVisible = bean.isSelected
        binding.ivCancel.setOnClickListener {
            listener.onCancelClick()
        }
        binding.root.setOnClickListener {
            if (position == itemCount) {
                listener.onCustomClick()
            } else {
                listener.onDateClick(position)
            }
            listener.onDateClick(position)
        }

    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemDateBinding {
        return ItemDateBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(binding: ItemDateBinding, viewType: Int): BaseViewHolder {
        return BaseViewHolder(binding)
    }

    interface OnDateClickListener {
        fun onCustomClick()
        fun onDateClick(position: Int)
        fun onCancelClick()
    }
}