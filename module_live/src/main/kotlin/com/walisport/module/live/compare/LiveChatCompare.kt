package com.walisport.module.live.compare

import androidx.recyclerview.widget.DiffUtil
import com.walisport.module.live.data.model.LiveChatBean

class LiveChatCompare :DiffUtil.ItemCallback<LiveChatBean>() {
    override fun areItemsTheSame(oldItem: LiveChatBean, newItem: LiveChatBean): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: LiveChatBean, newItem: LiveChatBean): Boolean {
        return oldItem.name == newItem.name
    }
}