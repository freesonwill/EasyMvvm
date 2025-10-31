package com.walisport.module.topup.ui.adapter.compare

import androidx.recyclerview.widget.DiffUtil
import com.walisport.module.topup.data.entity.CoinBean

class CoinCompare : DiffUtil.ItemCallback<CoinBean>() {

    override fun areItemsTheSame(
        oldItem: CoinBean,
        newItem: CoinBean
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: CoinBean,
        newItem: CoinBean
    ): Boolean {
        return oldItem == newItem
    }
}