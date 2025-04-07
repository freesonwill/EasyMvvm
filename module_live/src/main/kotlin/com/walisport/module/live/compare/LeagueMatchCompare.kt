package com.walisport.module.live.compare

import androidx.recyclerview.widget.DiffUtil
import com.walisport.module.live.data.model.LeagueMatchBean

class LeagueMatchCompare : DiffUtil.ItemCallback<LeagueMatchBean>() {

    override fun areItemsTheSame(oldItem: LeagueMatchBean, newItem: LeagueMatchBean): Boolean {
        return oldItem.homeTeamName == newItem.homeTeamName || oldItem.awayTeamName == newItem.awayTeamName || oldItem.isWeek == newItem.isWeek
    }

    override fun areContentsTheSame(oldItem: LeagueMatchBean, newItem: LeagueMatchBean): Boolean {
        return oldItem == newItem
    }
}