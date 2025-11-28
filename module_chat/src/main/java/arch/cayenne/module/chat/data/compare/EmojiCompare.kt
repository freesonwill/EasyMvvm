package arch.cayenne.module.chat.data.compare

import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.module.chat.data.model.EmojiModel

class EmojiCompare: DiffUtil.ItemCallback<EmojiModel>() {
    override fun areItemsTheSame(oldItem: EmojiModel, newItem: EmojiModel): Boolean {
        return oldItem.resId == newItem.resId
    }

    override fun areContentsTheSame(oldItem: EmojiModel, newItem: EmojiModel): Boolean {
    return oldItem.resId == newItem.resId
    }
}