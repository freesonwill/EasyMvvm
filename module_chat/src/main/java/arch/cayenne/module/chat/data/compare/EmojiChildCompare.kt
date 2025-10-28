package arch.cayenne.module.chat.data.compare

import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.module.chat.data.model.EmojiData

/**
 * @author: wenxi
 * @date: 23/10/25 14:40
 * @description:
 */class EmojiChildCompare: DiffUtil.ItemCallback<List<EmojiData>>(){
    override fun areItemsTheSame(oldItem: List<EmojiData>, newItem: List<EmojiData>): Boolean {
        return oldItem.size == newItem.size
    }

    override fun areContentsTheSame(oldItem: List<EmojiData>, newItem: List<EmojiData>): Boolean {
    return  oldItem.size == newItem.size
    }

}