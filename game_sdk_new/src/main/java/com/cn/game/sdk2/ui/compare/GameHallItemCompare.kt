package com.cn.game.sdk2.ui.compare

import androidx.recyclerview.widget.DiffUtil
import com.cn.game.sdk2.data.bean.GameHallItem

class GameHallItemCompare: DiffUtil.ItemCallback<GameHallItem>() {
    override fun areItemsTheSame(oldItem: GameHallItem, newItem: GameHallItem): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: GameHallItem, newItem: GameHallItem): Boolean {
        return oldItem == newItem
    }
}