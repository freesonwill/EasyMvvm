package com.walisport.module.live.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import com.walisport.module.live.R
import com.walisport.module.live.data.model.MediaSource
import com.walisport.module.live.data.model.MediaSourceType
import com.walisport.module.live.databinding.AdapterLiveMediaSourceItemSimpleLayoutBinding

/**
 * 视频源banner中，RecyclerView使用的Adapter
 */
class LiveMediaSourceSimpleAdapter(compare: DiffUtil.ItemCallback<MediaSource>) :
    BaseAdapter<MediaSource, LiveMediaSourceSimpleAdapter.ViewHolder, ViewBinding>(
        compare
    ) {

    inner class ViewHolder(binding: ViewBinding) : BaseViewHolder(binding) {
        private val viewBinding: AdapterLiveMediaSourceItemSimpleLayoutBinding =
            binding as AdapterLiveMediaSourceItemSimpleLayoutBinding


        fun updateItem(position: Int) {
            val item = getItem(position)

            if (item.mediaSourceType == MediaSourceType.VIDEO) {
                viewBinding.tvTitle.text = item.videoSourceBean?.title
                viewBinding.tvSubtitle.text = item.videoSourceBean?.subTitle

                if (item.videoSourceBean?.isPlaying == true) {
                    viewBinding.root.setBackgroundResource(R.drawable.bg_media_source_simple_item_selected)
                } else {
                    viewBinding.root.setBackgroundResource(R.drawable.bg_media_source_simple_item_unselected)
                }

                viewBinding.root.setOnClickListener {
                    listener(item)
                }
            } else if (item.mediaSourceType == MediaSourceType.ANIMATION) {
                viewBinding.tvTitle.text = R.string.media_source_animation_title.getString()
                viewBinding.tvSubtitle.text = R.string.media_source_animation_subtitle.getString()

                if (item.isPlaying) {
                    viewBinding.root.setBackgroundResource(R.drawable.bg_media_source_simple_item_selected)
                } else {
                    viewBinding.root.setBackgroundResource(R.drawable.bg_media_source_simple_item_unselected)
                }

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
        val binding = AdapterLiveMediaSourceItemSimpleLayoutBinding.inflate(inflater, parent, false)
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