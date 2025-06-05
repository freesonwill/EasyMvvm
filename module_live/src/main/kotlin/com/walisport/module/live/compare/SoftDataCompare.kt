package com.walisport.module.live.compare

import androidx.recyclerview.widget.DiffUtil
import com.walisport.module.live.data.model.SoftData

/**
 * @author: wenxi
 * @date: 4/6/25 10:00
 * @description:
 */
class SoftDataCompare : DiffUtil.ItemCallback<SoftData>() {
    override fun areItemsTheSame(oldItem: SoftData, newItem: SoftData): Boolean {
        return oldItem.emojiType == newItem.emojiType
    }

    override fun areContentsTheSame(oldItem: SoftData, newItem: SoftData): Boolean {
        return oldItem.emojiType == newItem.emojiType
    }
}