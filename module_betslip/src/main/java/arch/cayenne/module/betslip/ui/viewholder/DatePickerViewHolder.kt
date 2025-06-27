package arch.cayenne.module.betslip.ui.viewholder

import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.module.betslip.data.model.DateFilterBean
import arch.cayenne.module.betslip.databinding.ItemDateBinding
import arch.cayenne.module.betslip.ui.adapter.DatePickerAdapter

class DatePickerViewHolder(private val mBinding: ItemDateBinding) : BaseViewHolder(mBinding) {

    fun init(bean: DateFilterBean, listener: DatePickerAdapter.OnDateClickListener, itemCount: Int) {
        mBinding.tvTitle.text = bean.title
        mBinding.ivCancel.setOnClickListener {
            listener.onCancelClick()
        }
        mBinding.root.setOnClickListener {
            if (adapterPosition == itemCount -1) {
                listener.onCustomClick()
            } else {
                listener.onDateClick(adapterPosition)
            }
        }
        setSelected(bean.isSelected, itemCount)
    }

    private fun setSelected(isSelected: Boolean, itemCount: Int) {
        mBinding.ivCancel.isVisible = isSelected
        mBinding.clTitle.isEnabled = isSelected
        if (isSelected) {
            mBinding.tvTitle.setTextColor(ContextCompat.getColor(itemView.context, arch.cayenne.lib.common.R.color.brand_color))
        } else {
            mBinding.tvTitle.setTextColor(ContextCompat.getColor(itemView.context, arch.cayenne.lib.common.R.color.secondary_text))
        }
        mBinding.ivCancel.isVisible = adapterPosition == itemCount - 1 && isSelected
    }
}