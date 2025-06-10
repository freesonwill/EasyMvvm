package com.walisport.module.live.compare

import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.lib.websocket.chat.data.ChatMsg
import com.walisport.module.live.data.model.LiveChatBean

class LiveChatCompare :DiffUtil.ItemCallback<ChatMsg>() {
    override fun areItemsTheSame(oldItem: ChatMsg, newItem: ChatMsg): Boolean {
        return oldItem.msgId == newItem.msgId
    }

    override fun areContentsTheSame(oldItem: ChatMsg, newItem: ChatMsg): Boolean {
        return oldItem.msgId == newItem.msgId
    }
}