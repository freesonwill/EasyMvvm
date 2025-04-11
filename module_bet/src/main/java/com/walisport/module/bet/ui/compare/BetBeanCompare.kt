package com.walisport.module.bet.ui.compare

import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.lib.database.entity.BetBean

class BetBeanCompare: DiffUtil.ItemCallback<BetBean>() {
    override fun areItemsTheSame(oldItem: BetBean, newItem: BetBean): Boolean {
        return oldItem.gameId == newItem.gameId
    }

    override fun areContentsTheSame(oldItem: BetBean, newItem: BetBean): Boolean {
        return oldItem == newItem
    }
}