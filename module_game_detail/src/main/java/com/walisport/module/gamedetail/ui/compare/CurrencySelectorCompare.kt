package com.walisport.module.gamedetail.ui.compare

import androidx.recyclerview.widget.DiffUtil
import com.walisport.module.gamedetail.data.model.CurrencyInfoBean
import com.walisport.module.gamedetail.data.model.PlayerRankingBean

class CurrencySelectorCompare: DiffUtil.ItemCallback<CurrencyInfoBean>() {
    override fun areItemsTheSame(oldItem: CurrencyInfoBean, newItem: CurrencyInfoBean): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: CurrencyInfoBean,
        newItem: CurrencyInfoBean
    ): Boolean {
        return oldItem == newItem
    }
}