package arch.cayenne.module.chat.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.ui.adapter.RecyclerItemListener
import arch.cayenne.module.chat.data.compare.ChatPersonalCompare
import arch.cayenne.module.chat.data.model.ChatPersonalData
import arch.cayenne.module.chat.databinding.ItemPersonalDialogNameLayoutBinding
import arch.cayenne.module.chat.databinding.ItemPersonalDialogNormalLayoutBinding

/**
 * @author: wenxi
 * @date: 6/11/25 10:46
 * @description:
 */
class ChatPersonalAdapter :
    BaseAdapter<ChatPersonalData, ChatPersonalAdapter.ChatPersonalViewHolder, ViewBinding>(
        ChatPersonalCompare()
    ) {
    private var itemClick: RecyclerItemListener<ChatPersonalData>? = null
    fun setRecyclerItemListener(itemListener: RecyclerItemListener<ChatPersonalData>) {
        this.itemClick = itemListener
    }


    inner class ChatPersonalViewHolder(binding: ViewBinding) : BaseViewHolder(binding) {
        init {
            initClickListener()
        }

        private fun initClickListener() {
            val nBinding = binding
            when (nBinding) {
                is ItemPersonalDialogNormalLayoutBinding -> {

                    nBinding.main.setOnClickListener {
                        val position = it.tag as Int
                        itemClick?.onItemClick(getItem(position), position)
                    }
                }

                else -> {}
            }
        }

        fun updateText(text: String, position: Int) {
            val nBinding = binding
            when (nBinding) {
                is ItemPersonalDialogNameLayoutBinding -> {
                    nBinding.tvName.text = text
                    nBinding.main.tag = position
                }

                is ItemPersonalDialogNormalLayoutBinding -> {
                    nBinding.tv.text = text
                    nBinding.main.tag = position
                    if (position == currentList.size - 1) {
                        nBinding.line.isVisible = false
                    }
                }

                else -> {}
            }

        }
    }

    override fun convertPlus(holder: ChatPersonalViewHolder, binding: ViewBinding, position: Int) {
        holder.updateText(getItem(position).text, position)

    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ViewBinding {
        return if (viewType == 0) ItemPersonalDialogNormalLayoutBinding.inflate(
            inflater,
            parent,
            false
        ) else ItemPersonalDialogNameLayoutBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(binding: ViewBinding, viewType: Int): ChatPersonalViewHolder {
        return ChatPersonalViewHolder(binding)
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).type
    }

}
