package arch.cayenne.module.betslip.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.module.betslip.data.model.DateFilterBean
import arch.cayenne.module.betslip.databinding.ItemDateBinding
import arch.cayenne.module.betslip.ui.compare.DatePickerCompare

class DatePickerAdapter(private val listener: OnDateClickListener): BaseAdapter<DateFilterBean, BaseViewHolder, ItemDateBinding>(
    DatePickerCompare()
) {
    override fun convertPlus(holder: BaseViewHolder, binding: ItemDateBinding, position: Int) {
       val bean = getItem(position)
        binding.tvTitle.text = bean.title
        if (bean.isSelected) {
            binding.tvTitle.setTextColor(ContextCompat.getColor(holder.itemView.context, arch.cayenne.lib.common.R.color.brand_color))
        } else {
            binding.tvTitle.setTextColor(ContextCompat.getColor(holder.itemView.context, arch.cayenne.lib.common.R.color.secondary_text))
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
        binding.ivCancel.isVisible = position == itemCount - 1 && bean.isSelected
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