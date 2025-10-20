package com.walisport.module.topup.ui.adapter.compare

import androidx.recyclerview.widget.DiffUtil
import com.walisport.module.topup.data.entity.RechargeMethodBean

class RechargeMethodCompare : DiffUtil.ItemCallback<RechargeMethodBean>() {

    override fun areItemsTheSame(
        oldItem: RechargeMethodBean,
        newItem: RechargeMethodBean
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: RechargeMethodBean,
        newItem: RechargeMethodBean
    ): Boolean {
        return oldItem == newItem
    }
}