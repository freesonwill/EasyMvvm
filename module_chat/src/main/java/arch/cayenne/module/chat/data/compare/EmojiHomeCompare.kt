package arch.cayenne.module.chat.data.compare

import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.module.chat.data.model.EmojiHomeData

/**
 * @author: wenxi
 * @date: 4/6/25 10:00
 * @description:
 */
class EmojiHomeCompare : DiffUtil.ItemCallback<EmojiHomeData>() {
    override fun areItemsTheSame(oldItem: EmojiHomeData, newItem: EmojiHomeData): Boolean {
        return oldItem.emojiType == newItem.emojiType
    }

    override fun areContentsTheSame(oldItem: EmojiHomeData, newItem: EmojiHomeData): Boolean {
        return oldItem.emojiType == newItem.emojiType
    }
}