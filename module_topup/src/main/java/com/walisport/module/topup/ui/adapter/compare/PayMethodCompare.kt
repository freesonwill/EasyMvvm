package com.walisport.module.topup.ui.adapter.compare

import androidx.recyclerview.widget.DiffUtil
import com.walisport.module.topup.data.entity.PayMethodBean

class PayMethodCompare : DiffUtil.ItemCallback<PayMethodBean>() {

    override fun areItemsTheSame(
        oldItem: PayMethodBean,
        newItem: PayMethodBean
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: PayMethodBean,
        newItem: PayMethodBean
    ): Boolean {
        return oldItem == newItem
    }
}