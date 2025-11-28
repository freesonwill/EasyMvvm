package arch.cayenne.module.chat.data.compare

import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.lib.websocket.chat.data.ChatMsg
import arch.cayenne.module.chat.data.model.ChatMsgPageBean

class ChatCompare :DiffUtil.ItemCallback<ChatMsgPageBean>() {
    override fun areItemsTheSame(oldItem: ChatMsgPageBean, newItem: ChatMsgPageBean): Boolean {
        return oldItem.msgId == newItem.msgId
    }

    override fun areContentsTheSame(oldItem: ChatMsgPageBean, newItem: ChatMsgPageBean): Boolean {
        return oldItem.msgId == newItem.msgId
    }
}