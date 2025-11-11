package com.walisport.module.live.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.walisport.module.live.R
import com.walisport.module.live.data.model.MediaSource
import com.walisport.module.live.data.model.MediaSourceType
import com.walisport.module.live.databinding.AdapterLiveMediaSourceItemBinding

/**
 * 竖屏播放时的媒体源页面，RecyclerView使用的Adapter
 */
class LiveMediaSourceAdapter(compare: DiffUtil.ItemCallback<MediaSource>) :
    BaseAdapter<MediaSource, LiveMediaSourceAdapter.ViewHolder, ViewBinding>(
        compare
    ) {

    inner class ViewHolder(binding: ViewBinding) : BaseViewHolder(binding) {
        private val viewBinding: AdapterLiveMediaSourceItemBinding =
            binding as AdapterLiveMediaSourceItemBinding


        fun updateItem(position: Int) {
            val item = getItem(position)

            if (item.mediaSourceType == MediaSourceType.VIDEO) {
                viewBinding.tvTitle.text = item.videoSourceBean?.title
                viewBinding.tvSubtitle.text = item.videoSourceBean?.subTitle


                if (item.isPlaying) {
                    viewBinding.playingBkg.visibility = View.VISIBLE
                    viewBinding.animationView.playAnimation()
                } else {
                    viewBinding.playingBkg.visibility = View.GONE
                    viewBinding.animationView.pauseAnimation()
                }

                Glide.with(viewBinding.ivThumb).load(item.videoSourceBean?.thumb)
                    .placeholder(R.drawable.live_video_source_thumb_placeholder)
                    .apply(RequestOptions().transform(CenterCrop()))
                    .into(viewBinding.ivThumb)

                viewBinding.root.setOnClickListener {
                    listener(item)
                }
            } else if (item.mediaSourceType == MediaSourceType.ANIMATION) {
                viewBinding.tvTitle.text = R.string.media_source_animation_title.getString()
                viewBinding.tvSubtitle.text = R.string.media_source_animation_subtitle.getString()

                if (item.isPlaying) {
                    viewBinding.playingBkg.visibility = View.VISIBLE
                    viewBinding.animationView.playAnimation()
                } else {
                    viewBinding.playingBkg.visibility = View.GONE
                    viewBinding.animationView.pauseAnimation()
                }

                Glide.with(viewBinding.ivThumb).load("")
                    .placeholder(R.drawable.live_video_source_thumb_placeholder)
                    .apply(RequestOptions().transform(CenterCrop()))
                    .into(viewBinding.ivThumb)

                viewBinding.root.setOnClickListener {
                    listener(item)
                }
            }

        }
    }

    private lateinit var listener: (MediaSource) -> Unit

    fun setOnClickListener(listener: (MediaSource) -> Unit) {
        this.listener = listener
    }

    override fun convertPlus(
        holder: ViewHolder,
        binding: ViewBinding,
        position: Int
    ) {
        holder.updateItem(position)
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ViewBinding {
        val binding =
            AdapterLiveMediaSourceItemBinding.inflate(inflater, parent, false)
        return binding
    }

    override fun createViewHolder(
        binding: ViewBinding,
        viewType: Int
    ): ViewHolder {
        val holder = ViewHolder(binding)
        return holder
    }
}