package arch.cayenne.module.chat.ui.adapter

import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.ui.adapter.RecyclerItemListener
import arch.cayenne.module.chat.data.compare.EmojiCompare
import arch.cayenne.module.chat.data.constants.EmojiTypeEnum
import arch.cayenne.module.chat.data.model.EmojiModel
import arch.cayenne.module.chat.databinding.ItemEmojiHorizontalLayoutBinding
import arch.cayenne.module.chat.databinding.ItemRecyclerHorizontalBidLayoutBinding


class EmojiItemAdapter() :
    BaseAdapter<EmojiModel, EmojiItemAdapter.LiveEmojiViewHolder, ViewBinding>(
        EmojiCompare()
    ) {
    private var itemListener: RecyclerItemListener<EmojiModel>? = null

    fun setItemListener(listener: RecyclerItemListener<EmojiModel>?) {
        this.itemListener = listener
    }

    inner class LiveEmojiViewHolder(binding: ViewBinding) : BaseViewHolder(binding) {
        fun setListener(listener: OnClickListener) {
            when (binding) {
                is ItemEmojiHorizontalLayoutBinding -> {
                    val nBinding = binding as ItemEmojiHorizontalLayoutBinding
                    nBinding.container.setOnClickListener(listener)
                }

                is ItemRecyclerHorizontalBidLayoutBinding -> {
                    val nBinding = binding as ItemRecyclerHorizontalBidLayoutBinding
                    nBinding.container.setOnClickListener(listener)
                }

                else -> {}
            }
        }

        fun updateIv(resId: Int, position: Int) {
            when (binding) {
                is ItemEmojiHorizontalLayoutBinding -> {
                    val nBinding = binding as ItemEmojiHorizontalLayoutBinding
                    nBinding.container.tag = position
                    nBinding.iv.setImageResource(resId)
                }
                is ItemRecyclerHorizontalBidLayoutBinding -> {
                    val nBinding = binding as ItemRecyclerHorizontalBidLayoutBinding
                    nBinding.container.tag = position
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
            ItemEmojiHorizontalLayoutBinding.inflate(inflater, parent, false)
        else
            ItemRecyclerHorizontalBidLayoutBinding.inflate(inflater, parent, false)
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