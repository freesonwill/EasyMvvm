package com.cn.game.sdk2.ui.compare

import androidx.recyclerview.widget.DiffUtil
import com.cn.game.sdk2.data.enums.ChipBean

class ChipsCompare : DiffUtil.ItemCallback<ChipBean>() {
    override fun areItemsTheSame(
        oldItem: ChipBean,
        newItem: ChipBean
    ): Boolean {
        return oldItem.chip == newItem.chip
    }

    override fun areContentsTheSame(
        oldItem: ChipBean,
        newItem: ChipBean
    ): Boolean {
        return oldItem.isSelected == newItem.isSelected
    }
}