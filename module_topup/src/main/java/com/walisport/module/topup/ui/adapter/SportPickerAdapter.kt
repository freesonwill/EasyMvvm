package com.walisport.module.topup.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import com.walisport.module.topup.data.WithdrawFilterBean
import com.walisport.module.topup.databinding.ItemSportBinding
import com.walisport.module.topup.ui.adapter.compare.WithdrawPickerCompare

class SportPickerAdapter(private val listener: SportPickerListener) : BaseAdapter<WithdrawFilterBean, BaseViewHolder, ItemSportBinding>(
    WithdrawPickerCompare()
) {
    override fun convertPlus(holder: BaseViewHolder, binding: ItemSportBinding, position: Int) {
        val bean = getItem(position)
        binding.root.isSelected = bean.isSelected
        binding.groupArrow.isVisible = bean.isSelected
        binding.tvTitle.text = bean.txName
        val titleColor = if (bean.isSelected) {
            Color.parseColor("#00D271")
        } else {
            ContextCompat.getColor(
                holder.itemView.context,
                arch.cayenne.lib.common.R.color.secondary_text
            )
        }
        binding.tvTitle.setTextColor(titleColor)
        binding.root.setOnClickListener {
            listener.onSportSelected(bean.txId)
        }
        binding.tvTitle.setFontWeight(if (bean.isSelected) 500 else 400)
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemSportBinding {
        return ItemSportBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(binding: ItemSportBinding, viewType: Int): BaseViewHolder {
        return BaseViewHolder(binding)
    }

    interface SportPickerListener {
        fun onSportSelected(id: Int)
    }
}