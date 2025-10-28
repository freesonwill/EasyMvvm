package com.walisport.module.gamedetail.ui.compare

import androidx.recyclerview.widget.DiffUtil
import com.walisport.module.gamedetail.data.model.PlayerRankingBean

class PlayerRankingCompare: DiffUtil.ItemCallback<PlayerRankingBean>() {
    override fun areItemsTheSame(oldItem: PlayerRankingBean, newItem: PlayerRankingBean): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: PlayerRankingBean,
        newItem: PlayerRankingBean
    ): Boolean {
        return oldItem == newItem
    }
}