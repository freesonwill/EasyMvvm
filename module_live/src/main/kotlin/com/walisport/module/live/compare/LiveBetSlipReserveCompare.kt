package com.walisport.module.live.compare

import androidx.recyclerview.widget.DiffUtil
import galaxy.common.proto.Common

class LiveBetSlipReserveCompare:DiffUtil.ItemCallback<Common.ReserveOrder>() {
    override fun areItemsTheSame(
        oldItem: Common.ReserveOrder,
        newItem: Common.ReserveOrder
    ): Boolean {
        return oldItem.reserveId == newItem.reserveId
    }

    override fun areContentsTheSame(
        oldItem: Common.ReserveOrder,
        newItem: Common.ReserveOrder
    ): Boolean {
        return  oldItem.reserveId == newItem.reserveId
    }
}