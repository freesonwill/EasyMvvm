package com.cn.game.sdk2.ui.compare

import androidx.recyclerview.widget.DiffUtil
import com.cn.game.sdk2.websocket.bean.RoundInfoBean

class RoundInfoCompare : DiffUtil.ItemCallback<RoundInfoBean>() {
    override fun areItemsTheSame(oldItem: RoundInfoBean, newItem: RoundInfoBean): Boolean {
        return oldItem.roundId == newItem.roundId
    }

    override fun areContentsTheSame(oldItem: RoundInfoBean, newItem: RoundInfoBean): Boolean {
        return oldItem == newItem
    }
}