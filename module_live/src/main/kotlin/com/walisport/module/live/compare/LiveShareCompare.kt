package com.walisport.module.live.compare

import androidx.recyclerview.widget.DiffUtil
import com.walisport.module.live.data.model.LiveShareBean

class LiveShareCompare : DiffUtil.ItemCallback<LiveShareBean>() {
    override fun areItemsTheSame(oldItem: LiveShareBean, newItem: LiveShareBean): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: LiveShareBean, newItem: LiveShareBean): Boolean {
        return oldItem == newItem
    }
}