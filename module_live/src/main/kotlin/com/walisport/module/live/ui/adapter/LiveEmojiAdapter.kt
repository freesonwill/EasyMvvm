package com.walisport.module.live.ui.adapter

import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.ui.adapter.RecyclerItemListener
import com.bumptech.glide.Glide
import com.walisport.module.live.compare.LiveEmojiCompare
import com.walisport.module.live.data.constants.EmojiTypeEnum
import com.walisport.module.live.data.model.EmojiData
import com.walisport.module.live.databinding.ItemBidEmojiLayoutBinding
import com.walisport.module.live.databinding.ItemEmojiLayoutBinding


class LiveEmojiAdapter() :
    BaseAdapter<EmojiData, LiveEmojiAdapter.LiveEmojiViewHolder, ViewBinding>(LiveEmojiCompare()) {
    private var itemListener: RecyclerItemListener<EmojiData>? = null

    fun setItemListener(listener: RecyclerItemListener<EmojiData>?) {
        this.itemListener = listener
    }

    inner class LiveEmojiViewHolder(binding: ViewBinding) : BaseViewHolder(binding) {
        fun setListener(listener: OnClickListener) {
            when (binding) {
                is ItemEmojiLayoutBinding -> {
                    val nBinding = binding as ItemEmojiLayoutBinding
                    nBinding.iv.setOnClickListener(listener)
                }

                is ItemBidEmojiLayoutBinding -> {
                    val nBinding = binding as ItemBidEmojiLayoutBinding
                    nBinding.iv.setOnClickListener(listener)
                }

                else -> {}
            }
        }

        fun updateIv(resId: Int, position: Int) {
            when (binding) {
                is ItemEmojiLayoutBinding -> {
                    val nBinding = binding as ItemEmojiLayoutBinding
                    nBinding.iv.tag = position
                    nBinding.iv.setImageResource(resId)
                }

                is ItemBidEmojiLayoutBinding -> {
                    val nBinding = binding as ItemBidEmojiLayoutBinding
                    nBinding.iv.tag = position
                    nBinding.iv.setImageResource(resId)
                }

                else -> {}
            }

        }

    }

    override fun convertPlus(holder: LiveEmojiViewHolder, binding: ViewBinding, position: Int) {

        val item = getItem(position)
        holder.updateIv(item.resId, position)
    }


    override fun createViewBinding(
        inflater: LayoutInflater, parent: ViewGroup, viewType: Int
    ): ViewBinding {
        val binding = if (viewType == EmojiTypeEnum.NORMAL.value)
            ItemEmojiLayoutBinding.inflate(inflater, parent, false)
        else
            ItemBidEmojiLayoutBinding.inflate(inflater, parent, false)
        return binding
    }

    override fun createViewHolder(binding: ViewBinding, viewType: Int): LiveEmojiViewHolder {
        val holder = LiveEmojiViewHolder(binding)
        holder.setListener {
            val position = it.tag as Int
            itemListener?.onItemClick(getItem(position), position)
        }
        return holder
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).key.contains("bid")) EmojiTypeEnum.BID.value else EmojiTypeEnum.NORMAL.value
    }

}