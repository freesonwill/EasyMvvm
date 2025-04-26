package com.walisport.module.live.compare

import androidx.recyclerview.widget.DiffUtil
import galaxy.common.proto.Common

class LiveBetSlipCompare : DiffUtil.ItemCallback<Common.Order>() {
    override fun areItemsTheSame(oldItem: Common.Order, newItem: Common.Order): Boolean {
        return oldItem.betId == newItem.betId
    }

    override fun areContentsTheSame(oldItem: Common.Order, newItem: Common.Order): Boolean {
        return oldItem.betId == newItem.betId
    }
}