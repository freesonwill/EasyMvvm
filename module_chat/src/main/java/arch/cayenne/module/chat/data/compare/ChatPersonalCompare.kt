package arch.cayenne.module.chat.data.compare

import androidx.recyclerview.widget.DiffUtil.ItemCallback
import arch.cayenne.module.chat.data.model.ChatPersonalData

/**
 * @author: wenxi
 * @date: 6/11/25 10:49
 * @description:
 */
class ChatPersonalCompare:ItemCallback<ChatPersonalData>() {
    override fun areItemsTheSame(oldItem: ChatPersonalData, newItem: ChatPersonalData): Boolean {
        return oldItem.text == newItem.text
    }

    override fun areContentsTheSame(oldItem: ChatPersonalData, newItem: ChatPersonalData): Boolean {
      return oldItem.text == newItem.text
    }
}