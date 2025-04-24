package com.walisport.module.live.compare

import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.lib.database.entity.VideoSourceBean

class VideoSourceBeanCompare : DiffUtil.ItemCallback<VideoSourceBean>() {

    override fun areItemsTheSame(oldItem: VideoSourceBean, newItem: VideoSourceBean): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: VideoSourceBean, newItem: VideoSourceBean): Boolean {
        return oldItem == newItem
    }
}