package com.walisport.module.live.compare

import androidx.recyclerview.widget.DiffUtil
import com.walisport.module.live.data.model.MatchBean

class LeagueMatchCompare : DiffUtil.ItemCallback<MatchBean>() {

    override fun areItemsTheSame(oldItem: MatchBean, newItem: MatchBean): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: MatchBean, newItem: MatchBean): Boolean {
        return oldItem == newItem
    }
}