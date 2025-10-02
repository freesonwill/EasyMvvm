package com.walisport.module.live.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import com.walisport.module.live.R
import com.walisport.module.live.data.model.VideoResolutionBean
import com.walisport.module.live.databinding.ItemVideoResolutionBinding

class VideoResolutionAdapter(
    private val onItemClick: (String) -> Unit
) : BaseAdapter<VideoResolutionBean, VideoResolutionViewHolder, ItemVideoResolutionBinding>(
    VideoResolutionCompare()
) {

    override fun convertPlus(
        holder: VideoResolutionViewHolder,
        binding: ItemVideoResolutionBinding,
        position: Int
    ) {
        holder.initView()
        val item = getItem(position)
        binding.tvTitle.text = item.resolution
        if (item.selected) {
            binding.tvTitle.setTextColorRes(R.color.video_resolution_selected_text_color)
        } else {
            binding.tvTitle.setTextColorRes(R.color.video_resolution_unselected_text_color)
        }
        binding.root.setOnClickListener {
            onItemClick(item.resolution)
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemVideoResolutionBinding {
        return ItemVideoResolutionBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(
        binding: ItemVideoResolutionBinding,
        viewType: Int
    ): VideoResolutionViewHolder {
        return VideoResolutionViewHolder(binding)
    }
}

class VideoResolutionCompare : DiffUtil.ItemCallback<VideoResolutionBean>() {
    override fun areItemsTheSame(
        oldItem: VideoResolutionBean,
        newItem: VideoResolutionBean
    ): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(
        oldItem: VideoResolutionBean,
        newItem: VideoResolutionBean
    ): Boolean {
        return oldItem.resolution == newItem.resolution
    }
}

class VideoResolutionViewHolder(protected val mBinding: ItemVideoResolutionBinding) :
    BaseViewHolder(mBinding) {

    fun initView() {
    }
}