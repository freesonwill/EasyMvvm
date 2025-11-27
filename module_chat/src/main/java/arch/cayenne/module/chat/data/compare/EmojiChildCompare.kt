package arch.cayenne.module.chat.data.compare

import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.module.chat.data.model.EmojiModel

/**
 * @author: wenxi
 * @date: 23/10/25 14:40
 * @description:
 */class EmojiChildCompare: DiffUtil.ItemCallback<List<EmojiModel>>(){
    override fun areItemsTheSame(oldItem: List<EmojiModel>, newItem: List<EmojiModel>): Boolean {
        return oldItem.size == newItem.size
    }

    override fun areContentsTheSame(oldItem: List<EmojiModel>, newItem: List<EmojiModel>): Boolean {
    return  oldItem.size == newItem.size
    }

}