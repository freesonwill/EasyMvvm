package com.walisport.module.live.compare

import androidx.recyclerview.widget.DiffUtil
import com.walisport.module.live.data.model.MatchEventBean

class MatchEventCompare : DiffUtil.ItemCallback<MatchEventBean>() {

    override fun areItemsTheSame(oldItem: MatchEventBean, newItem: MatchEventBean): Boolean {
        return oldItem.time == newItem.time
    }

    override fun areContentsTheSame(oldItem: MatchEventBean, newItem: MatchEventBean): Boolean {
        return oldItem == newItem
    }
}