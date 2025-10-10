package arch.cayenne.module.chat.data.compare

import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.lib.websocket.chat.data.ChatMsg

class LiveChatCompare :DiffUtil.ItemCallback<ChatMsg>() {
    override fun areItemsTheSame(oldItem: ChatMsg, newItem: ChatMsg): Boolean {
        return oldItem.msgId == newItem.msgId
    }

    override fun areContentsTheSame(oldItem: ChatMsg, newItem: ChatMsg): Boolean {
        return oldItem.msgId == newItem.msgId
    }
}