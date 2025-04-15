package com.walisport.module.live.compare

import androidx.recyclerview.widget.DiffUtil
import com.walisport.module.live.data.model.VideoSourceBean

class VideoSourceCompare : DiffUtil.ItemCallback<VideoSourceBean>() {

    override fun areItemsTheSame(oldItem: VideoSourceBean, newItem: VideoSourceBean): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: VideoSourceBean, newItem: VideoSourceBean): Boolean {
        return oldItem == newItem
    }
}