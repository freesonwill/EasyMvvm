package com.walisport.module.topup.ui.adapter.compare

import androidx.recyclerview.widget.DiffUtil
import com.walisport.module.topup.data.entity.BankBean

class BankCompare : DiffUtil.ItemCallback<BankBean>() {

    override fun areItemsTheSame(
        oldItem: BankBean,
        newItem: BankBean
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: BankBean,
        newItem: BankBean
    ): Boolean {
        return oldItem == newItem
    }
}