package com.walisport.module.live.compare

import androidx.recyclerview.widget.DiffUtil
import com.walisport.module.live.data.model.EmojiData

class LiveEmojiCompare: DiffUtil.ItemCallback<EmojiData>() {
    override fun areItemsTheSame(oldItem: EmojiData, newItem: EmojiData): Boolean {
        return oldItem.resId == newItem.resId
    }

    override fun areContentsTheSame(oldItem: EmojiData, newItem: EmojiData): Boolean {
    return oldItem.resId == newItem.resId
    }
}