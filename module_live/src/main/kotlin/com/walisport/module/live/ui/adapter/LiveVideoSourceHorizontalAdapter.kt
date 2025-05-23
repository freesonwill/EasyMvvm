package com.walisport.module.live.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.database.entity.VideoSourceBean
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.walisport.module.live.R
import com.walisport.module.live.databinding.AdapterLiveVideoSourceItemHorizontalLayoutBinding

/**
 * 竖屏播放时的视频源页面，RecyclerView使用的Adapter
 */
class LiveVideoSourceHorizontalAdapter(compare: DiffUtil.ItemCallback<VideoSourceBean>) :
    BaseAdapter<VideoSourceBean, LiveVideoSourceHorizontalAdapter.LiveVideoSourceViewHolder, ViewBinding>(
        compare
    ) {

    inner class LiveVideoSourceViewHolder(binding: ViewBinding) : BaseViewHolder(binding) {
        private val viewBinding: AdapterLiveVideoSourceItemHorizontalLayoutBinding =
            binding as AdapterLiveVideoSourceItemHorizontalLayoutBinding



        fun updateItem(position: Int) {
            val item = getItem(position)
            viewBinding.tvTitle.text = item.title
            viewBinding.tvSubtitle.text = item.subTitle


            if (item.isPlaying) {
                viewBinding.playingBkg.visibility = View.VISIBLE
                viewBinding.animationView.playAnimation()
            } else {
                viewBinding.playingBkg.visibility = View.GONE
                viewBinding.animationView.pauseAnimation()
            }

            Glide.with(viewBinding.ivThumb).load(item.thumb)
                .placeholder(R.drawable.live_video_source_thumb_placeholder)
                .apply(RequestOptions().transform(CenterCrop()))
                .into(viewBinding.ivThumb)

            viewBinding.root.setOnClickListener{
                listener(item.id)
            }

        }
    }

    private lateinit var listener: (Int) -> Unit

    fun setOnClickListener(listener: (Int) -> Unit) {
        this.listener = listener
    }

    override fun convertPlus(
        holder: LiveVideoSourceViewHolder,
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
        val binding = AdapterLiveVideoSourceItemHorizontalLayoutBinding.inflate(inflater, parent, false)
        return binding
    }

    override fun createViewHolder(
        binding: ViewBinding,
        viewType: Int
    ): LiveVideoSourceViewHolder {
        val holder = LiveVideoSourceViewHolder(binding)
        return holder
    }
}