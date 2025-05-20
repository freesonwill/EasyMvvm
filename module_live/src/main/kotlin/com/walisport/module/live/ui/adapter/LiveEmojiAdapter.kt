package com.walisport.module.live.ui.adapter

import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.ui.adapter.RecyclerItemListener
import com.walisport.module.live.compare.LiveEmojiCompare
import com.walisport.module.live.data.model.EmojiData
import com.walisport.module.live.databinding.ItemBidEmojiLayoutBinding
import com.walisport.module.live.databinding.ItemEmojiLayoutBinding
import com.walisport.module.live.databinding.ItemLastEmojiLayoutBinding

class LiveEmojiAdapter :
    BaseAdapter<EmojiData, LiveEmojiAdapter.LiveEmojiViewHolder, ViewBinding>(LiveEmojiCompare()) {
    private var type: Int = 0 //id 0 bid/soccer 1
    private val TYPE_NORMAL = 0
    private val TYPE_LAST = 1
    private var itemListener: RecyclerItemListener<EmojiData>? = null


    fun setType(type: Int) {
        this.type = type
    }

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

                is ItemLastEmojiLayoutBinding -> {
                    val nBinding = binding as ItemLastEmojiLayoutBinding
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

                is ItemLastEmojiLayoutBinding -> {
                    val nBinding = binding as ItemLastEmojiLayoutBinding
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
//        if (position == itemCount - 1 && type == 0) {
//            val nBinding = binding as ItemEmojiLayoutBinding
//            val lp: ViewGroup.MarginLayoutParams = nBinding.iv.layoutParams as ViewGroup.MarginLayoutParams
//            lp.width = 89.dp2px
//            lp.height = 46.dp2px
//            lp.setMargins(-10.dp2px,0,0,0)
//            nBinding.iv.layoutParams = lp
//        }

        holder.updateIv(item.resId, position)
    }


    override fun createViewBinding(
        inflater: LayoutInflater, parent: ViewGroup, viewType: Int
    ): ViewBinding {
        val binding = if (type == 0 && viewType == TYPE_NORMAL) ItemEmojiLayoutBinding.inflate(
            inflater, parent, false
        )
        else if (type == 0 && viewType == TYPE_LAST) ItemLastEmojiLayoutBinding.inflate(
            inflater, parent, false
        )
        else ItemBidEmojiLayoutBinding.inflate(inflater, parent, false)
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

        return if (position == itemCount - 1) TYPE_LAST else TYPE_NORMAL
    }
}