package com.walisport.module.live.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import com.walisport.module.live.databinding.AdapterLiveBetContentItemLayoutBinding

class LiveBetContentAdapter(compare: DiffUtil.ItemCallback<String>) :
    BaseAdapter<String, LiveBetContentAdapter.LiveBetContentViewHolder, ViewBinding>(
        compare
    ) {
    inner class LiveBetContentViewHolder(binding: ViewBinding) : BaseViewHolder(binding) {
        private val viewBinding: AdapterLiveBetContentItemLayoutBinding =
            binding as AdapterLiveBetContentItemLayoutBinding
        init {
            setOnClickListener()
        }
        private fun setOnClickListener() {

        }
        fun updateItem(position: Int) {
            if (position==2){
                viewBinding.clRoot.isSelected = true
            }
            viewBinding.tvBetDuelLeft.text = getItem(position)
        }
    }
    override fun convertPlus(holder: LiveBetContentViewHolder, binding: ViewBinding, position: Int) {
        holder.updateItem(position)
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ViewBinding {
        val binding = AdapterLiveBetContentItemLayoutBinding.inflate(inflater,parent,false)
        return binding
    }
    override fun createViewHolder(
        binding: ViewBinding,
        viewType: Int
    ): LiveBetContentViewHolder {
        val holder = LiveBetContentViewHolder(binding)
        return holder
    }
}