package com.walisport.module.live.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.adapter.BaseAdapter
import arch.cayenne.lib.base.viewholder.BaseViewHolder
import com.walisport.module.live.databinding.AdapterLiveBetContentItemLayoutBinding
import com.walisport.module.live.databinding.AdapterLiveBetMenuContentItemLayoutBinding

class LiveBetMenuContentAdapter(compare: DiffUtil.ItemCallback<String>) :
    BaseAdapter<String, LiveBetMenuContentAdapter.LiveBetMenuContentViewHolder, ViewBinding>(
        compare
    ) {

    inner class LiveBetMenuContentViewHolder(binding: ViewBinding) : BaseViewHolder(binding) {
        private val viewBinding: AdapterLiveBetMenuContentItemLayoutBinding =
            binding as AdapterLiveBetMenuContentItemLayoutBinding
        init {
            setOnClickListener()
        }
        private fun setOnClickListener() {

        }
        fun updateItem(position: Int) {
            if (position==2){
                viewBinding.clRoot.isSelected = true
            }
            viewBinding.tvName.text = getItem(position)
        }
    }
    override fun convertPlus(holder: LiveBetMenuContentViewHolder, binding: ViewBinding, position: Int) {
        holder.updateItem(position)
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ViewBinding {
        val binding = AdapterLiveBetMenuContentItemLayoutBinding.inflate(inflater,parent,false)
        return binding
    }
    override fun createViewHolder(
        binding: ViewBinding,
        viewType: Int
    ): LiveBetMenuContentViewHolder {
        val holder = LiveBetMenuContentViewHolder(binding)
        return holder
    }
}