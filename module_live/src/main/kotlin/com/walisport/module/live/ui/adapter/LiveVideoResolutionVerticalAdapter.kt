package com.walisport.module.live.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.ResourceExt.getColor
import arch.cayenne.lib.common.utils.ext.ResourceExt.getDrawable
import com.walisport.module.live.R
import com.walisport.module.live.data.model.VideoResolutionBean
import com.walisport.module.live.databinding.AdapterLiveVideoResolutionItemLayoutBinding


/**
 * 视频横屏播放时的选择清晰度页, RecyclerView使用的Adapter
 */
class LiveVideoResolutionVerticalAdapter(compare: DiffUtil.ItemCallback<VideoResolutionBean>) :
    BaseAdapter<VideoResolutionBean, LiveVideoResolutionVerticalAdapter.LiveVideoResolutionViewHolder, ViewBinding>(
        compare
    ) {

    inner class LiveVideoResolutionViewHolder(binding: ViewBinding) : BaseViewHolder(binding) {
        private val viewBinding: AdapterLiveVideoResolutionItemLayoutBinding =
            binding as AdapterLiveVideoResolutionItemLayoutBinding

        fun updateItem(position: Int) {
            val item = getItem(position)
            viewBinding.tvTitle.text = item.resolution

            if (item.selected) {
                viewBinding.tvTitle.setTextColor(R.color.video_resolution_vertical_selected_text_color.getColor())
                viewBinding.tvTitle.background =
                    R.drawable.bg_video_resolution_item_vertical_selected.getDrawable()
            } else {
                viewBinding.tvTitle.setTextColor(R.color.video_resolution_vertical_unselected_text_color.getColor())
                viewBinding.tvTitle.background =
                    R.drawable.bg_video_resolution_item_vertical_unselected_white_blue.getDrawable()
            }


            viewBinding.root.setOnClickListener {
                listener(item.resolution)
            }

        }
    }

    private lateinit var listener: (String) -> Unit

    fun setOnClickListener(listener: (String) -> Unit) {
        this.listener = listener
    }

    override fun convertPlus(
        holder: LiveVideoResolutionViewHolder,
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
        val binding = AdapterLiveVideoResolutionItemLayoutBinding.inflate(inflater, parent, false)
        return binding
    }

    override fun createViewHolder(
        binding: ViewBinding,
        viewType: Int
    ): LiveVideoResolutionViewHolder {
        val holder = LiveVideoResolutionViewHolder(binding)
        return holder
    }
}