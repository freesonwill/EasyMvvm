package com.walisport.module.live.compare

import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.lib.database.entity.VideoSourceBean
import com.walisport.module.live.data.model.MediaSource

class MediaSourceBeanCompare : DiffUtil.ItemCallback<MediaSource>() {

    override fun areItemsTheSame(oldItem: MediaSource, newItem: MediaSource): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: MediaSource, newItem: MediaSource): Boolean {
        return oldItem == newItem
    }
}