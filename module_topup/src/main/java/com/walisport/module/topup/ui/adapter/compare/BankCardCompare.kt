package com.walisport.module.topup.ui.adapter.compare

import androidx.recyclerview.widget.DiffUtil
import com.walisport.module.topup.data.entity.BankCardBean

class BankCardCompare : DiffUtil.ItemCallback<BankCardBean>() {

    override fun areItemsTheSame(
        oldItem: BankCardBean,
        newItem: BankCardBean
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: BankCardBean,
        newItem: BankCardBean
    ): Boolean {
        return oldItem == newItem
    }
}