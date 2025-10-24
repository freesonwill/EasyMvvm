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
import arch.cayenne.module.chat.data.model.EmojiData
import arch.cayenne.module.chat.databinding.ItemBidEmojiLayoutBinding
import arch.cayenne.module.chat.databinding.ItemEmojiHotLayoutBinding
import arch.cayenne.module.chat.databinding.ItemEmojiLayoutBinding


class EmojiHotItemAdapter() :
    BaseAdapter<EmojiData, EmojiHotItemAdapter.LiveEmojiViewHolder, ItemEmojiHotLayoutBinding>(
        EmojiCompare()
    ) {
    private var itemListener: RecyclerItemListener<EmojiData>? = null

    fun setItemListener(listener: RecyclerItemListener<EmojiData>?) {
        this.itemListener = listener
    }

    inner class LiveEmojiViewHolder(binding: ItemEmojiHotLayoutBinding) : BaseViewHolder(binding) {
        fun setListener(listener: OnClickListener) {
            when (binding) {

                is ItemEmojiHotLayoutBinding -> {
                    val nBinding = binding as ItemEmojiHotLayoutBinding
                    nBinding.iv.setOnClickListener(listener)
                }

                else -> {}
            }
        }

        fun updateIv(resId: Int, position: Int) {
            when (binding) {
                is ItemEmojiHotLayoutBinding -> {
                    val nBinding = binding as ItemEmojiHotLayoutBinding
                    nBinding.iv.tag = position
                    nBinding.iv.setImageResource(resId)
                }
                else -> {}
            }

        }

    }

    override fun convertPlus(holder: LiveEmojiViewHolder, binding: ItemEmojiHotLayoutBinding, position: Int) {
        val item = getItem(position)
        holder.updateIv(item.resId, position)
    }


    override fun createViewBinding(
        inflater: LayoutInflater, parent: ViewGroup, viewType: Int
    ): ItemEmojiHotLayoutBinding {
        val binding = ItemEmojiHotLayoutBinding.inflate(inflater, parent, false)
        return binding
    }

    override fun createViewHolder(binding: ItemEmojiHotLayoutBinding, viewType: Int): LiveEmojiViewHolder {
        val holder = LiveEmojiViewHolder(binding)
        holder.setListener {
            val position = it.tag as Int
            itemListener?.onItemClick(getItem(position), position)
        }
        return holder
    }



}