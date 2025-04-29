package com.walisport.module.live.compare

import androidx.recyclerview.widget.DiffUtil
import galaxy.common.proto.Common

class LiveBetSlipSelectionCompare : DiffUtil.ItemCallback<Common.OrderSelection>() {
    override fun areItemsTheSame(
        oldItem: Common.OrderSelection,
        newItem: Common.OrderSelection
    ): Boolean {
        return oldItem.selectionId == newItem.selectionId
    }

    override fun areContentsTheSame(
        oldItem: Common.OrderSelection,
        newItem: Common.OrderSelection
    ): Boolean {
        return oldItem.selectionId == newItem.selectionId
    }
}