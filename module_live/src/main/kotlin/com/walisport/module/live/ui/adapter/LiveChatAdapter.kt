package com.walisport.module.live.ui.adapter

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.adapter.BaseAdapter
import arch.cayenne.lib.base.viewholder.BaseViewHolder
import com.walisport.module.live.R
import com.walisport.module.live.compare.LiveChatCompare
import com.walisport.module.live.data.model.LiveChatBean
import com.walisport.module.live.databinding.ItemLiveChatBinding

class LiveChatAdapter :
    BaseAdapter<LiveChatBean, LiveChatAdapter.LiveChatViewHolder, ViewBinding>(LiveChatCompare()) {

    class LiveChatViewHolder(binding: ViewBinding) : BaseViewHolder(binding) {
        val nBinding = binding as ItemLiveChatBinding

        fun setText(bean: LiveChatBean) {
            val first = "${bean.name}:"
            val second = bean.content

            val builder = SpannableStringBuilder()
            builder.append("$first  ")
            builder.setSpan(
                ForegroundColorSpan(
                    ContextCompat.getColor(
                        binding.root.context, R.color.live_name
                    )
                ), 0, first.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            builder.append(second)
            builder.setSpan(
                ForegroundColorSpan(
                    ContextCompat.getColor(
                        binding.root.context, R.color.secondary_text
                    )
                ), first.length, builder.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            nBinding.tv.text = builder
        }
    }

    override fun convertPlus(holder: LiveChatViewHolder, binding: ViewBinding, position: Int) {
        holder.setText(getItem(position))
    }

    override fun createViewBinding(
        inflater: LayoutInflater, parent: ViewGroup, viewType: Int
    ): ViewBinding {
        val binding = ItemLiveChatBinding.inflate(inflater, parent, false)
        return binding
    }

    override fun createViewHolder(binding: ViewBinding, viewType: Int): LiveChatViewHolder {
        val holder = LiveChatViewHolder(binding)
        return holder
    }

}