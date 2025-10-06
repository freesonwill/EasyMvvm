package com.walisport.module.live.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.database.entity.VideoSourceBean
import com.walisport.module.live.R
import com.walisport.module.live.databinding.AdapterLiveVideoSourceItemSimpleLayoutBinding

/**
 * 进入视频播放时的视频源页面，RecyclerView使用的Adapter
 */
class LiveVideoSourceSimpleAdapter(compare: DiffUtil.ItemCallback<VideoSourceBean>) :
    BaseAdapter<VideoSourceBean, LiveVideoSourceSimpleAdapter.LiveVideoSourceSimpleViewHolder, ViewBinding>(
        compare
    ) {

    inner class LiveVideoSourceSimpleViewHolder(binding: ViewBinding) : BaseViewHolder(binding) {
        private val viewBinding: AdapterLiveVideoSourceItemSimpleLayoutBinding =
            binding as AdapterLiveVideoSourceItemSimpleLayoutBinding


        fun updateItem(position: Int) {
            val item = getItem(position)
            viewBinding.tvTitle.text = item.title
            viewBinding.tvSubtitle.text = item.subTitle

            if (item.isPlaying) {
                viewBinding.root.setBackgroundResource(R.drawable.bg_video_source_simple_item_selected)
            } else {
                viewBinding.root.setBackgroundResource(R.drawable.bg_video_source_simple_item_unselected)
            }

            viewBinding.root.setOnClickListener {
                listener(item.id)
            }

        }
    }

    private lateinit var listener: (Int) -> Unit

    fun setOnClickListener(listener: (Int) -> Unit) {
        this.listener = listener
    }

    override fun convertPlus(
        holder: LiveVideoSourceSimpleViewHolder,
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
        val binding = AdapterLiveVideoSourceItemSimpleLayoutBinding.inflate(inflater, parent, false)
        return binding
    }

    override fun createViewHolder(
        binding: ViewBinding,
        viewType: Int
    ): LiveVideoSourceSimpleViewHolder {
        val holder = LiveVideoSourceSimpleViewHolder(binding)
        return holder
    }
}