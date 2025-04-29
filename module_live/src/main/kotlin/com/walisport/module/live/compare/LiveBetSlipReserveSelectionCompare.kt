package com.walisport.module.live.compare

import androidx.recyclerview.widget.DiffUtil
import galaxy.common.proto.Common

class LiveBetSlipReserveSelectionCompare: DiffUtil.ItemCallback<Common.ReserveOrderSelection>() {
    override fun areItemsTheSame(
        oldItem: Common.ReserveOrderSelection,
        newItem: Common.ReserveOrderSelection
    ): Boolean {
        return oldItem.selectionId == newItem.selectionId
    }

    override fun areContentsTheSame(
        oldItem: Common.ReserveOrderSelection,
        newItem: Common.ReserveOrderSelection
    ): Boolean {
    return oldItem.selectionId == newItem.selectionId
    }
}