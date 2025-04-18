package com.walisport.module.live.compare

import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.lib.database.entity.LiveVideoBean
import com.walisport.module.live.data.model.MatchLiveStreamBean

class VideoSourceCompare : DiffUtil.ItemCallback<LiveVideoBean>() {

    override fun areItemsTheSame(oldItem: LiveVideoBean, newItem: LiveVideoBean): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: LiveVideoBean, newItem: LiveVideoBean): Boolean {
        return oldItem == newItem
    }
}