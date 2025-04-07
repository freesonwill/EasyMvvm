package com.walisport.module.live.compare

import androidx.recyclerview.widget.DiffUtil
import com.walisport.module.live.data.model.StandingsBean

class StandingsCompare : DiffUtil.ItemCallback<StandingsBean>() {

    override fun areItemsTheSame(oldItem: StandingsBean, newItem: StandingsBean): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: StandingsBean, newItem: StandingsBean): Boolean {
        return oldItem == newItem
    }
}