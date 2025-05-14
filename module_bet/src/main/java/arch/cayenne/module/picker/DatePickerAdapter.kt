package arch.cayenne.module.picker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
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
        if (bean.isSelected) {
            binding.tvTitle.setTextColor(ContextCompat.getColor(holder.itemView.context, arch.cayenne.lib.res.R.color.brand_color))
        } else {
            binding.tvTitle.setTextColor(ContextCompat.getColor(holder.itemView.context, arch.cayenne.lib.res.R.color.secondary_text))
        }
        binding.ivCancel.isVisible = bean.isSelected
        binding.clTitle.isEnabled = bean.isSelected
        binding.ivCancel.setOnClickListener {
            listener.onCancelClick()
        }
        binding.root.setOnClickListener {
            if (position == itemCount - 1) {
                listener.onCustomClick()
            } else {
                listener.onDateClick(position)
            }
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