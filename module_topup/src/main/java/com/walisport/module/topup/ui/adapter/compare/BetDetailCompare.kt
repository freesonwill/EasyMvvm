package com.walisport.module.topup.ui.adapter.compare

import androidx.recyclerview.widget.DiffUtil
import com.walisport.module.topup.data.entity.BetDetailBean

class BetDetailCompare : DiffUtil.ItemCallback<BetDetailBean>() {

    override fun areItemsTheSame(
        oldItem: BetDetailBean,
        newItem: BetDetailBean
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: BetDetailBean,
        newItem: BetDetailBean
    ): Boolean {
        return oldItem == newItem
    }
}