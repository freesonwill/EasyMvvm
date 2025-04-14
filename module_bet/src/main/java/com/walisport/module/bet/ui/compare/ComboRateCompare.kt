package com.walisport.module.bet.ui.compare

import androidx.recyclerview.widget.DiffUtil
import com.walisport.module.bet.data.ComboRateBean

class ComboRateCompare: DiffUtil.ItemCallback<ComboRateBean>() {
    override fun areItemsTheSame(oldItem: ComboRateBean, newItem: ComboRateBean): Boolean {
        return oldItem.combo == newItem.combo && oldItem.money == newItem.money
    }

    override fun areContentsTheSame(oldItem: ComboRateBean, newItem: ComboRateBean): Boolean {
        return oldItem == newItem
    }
}