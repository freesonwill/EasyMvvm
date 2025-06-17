package com.walisport.module.message.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.base.ui.fragment.inflateMethod
import com.bumptech.glide.Glide
import com.walisport.module.message.R
import com.walisport.module.message.data.MessageCompare
import com.walisport.module.message.data.NotificationBean
import com.walisport.module.message.databinding.ItemMessageActivityBinding
import com.walisport.module.message.databinding.ItemMessageMatchBinding
import com.walisport.module.message.databinding.ItemMessageSystemBinding
import com.walisport.module.message.databinding.ItemMessageWalletBinding
import java.text.SimpleDateFormat
import java.util.Date

class MessageAdapter : BaseAdapter<NotificationBean, BaseViewHolder, ViewBinding>(
    MessageCompare()
) {
    private var clicklistener: OnClickListener? = null

    override fun convertPlus(holder: BaseViewHolder, binding: ViewBinding, position: Int) {
        val item = getItem(position)
        when (binding) {
            is ItemMessageSystemBinding -> {
                binding.tvMsgTime.text = getTime(item.createTime)
                binding.tvMsgTitle.text = item.title
                binding.tvMsgContent.text = item.content
                binding.ivMsgDelete.setOnClickListener {
                    clicklistener?.onDelete(item.id)
                }
            }
            is ItemMessageActivityBinding -> {
                binding.tvMsgTime.text = getTime(item.createTime)
                binding.tvMsgTitle.text = item.title
                binding.tvMsgContent.text = item.content
                binding.ivMsgDelete.setOnClickListener {
                    clicklistener?.onDelete(item.id)
                }
                binding.layDetail.setOnClickListener {
                    clicklistener?.onDetail(item.id)
                }
            }
            is ItemMessageMatchBinding -> {
                binding.tvMsgTime.text = getTime(item.createTime)
                binding.tvMsgTitle.text = item.title
                binding.tvMsgContent.text = item.content
                binding.ivMsgDelete.setOnClickListener {
                    clicklistener?.onDelete(item.id)
                }
                binding.layDetail.setOnClickListener {
                    clicklistener?.onDetail(item.id)
                }
                //Glide.with(binding.root).load(item.url).into(binding.ivMsgImage)
            }
            is ItemMessageWalletBinding -> {
                binding.ivMsgDelete.setOnClickListener {
                    clicklistener?.onDelete(item.id)
                }
                binding.layDetail.setOnClickListener {
                    clicklistener?.onDetail(item.id)
                }
            }
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater, parent: ViewGroup, viewType: Int
    ): ViewBinding {
        val clz = when (viewType) {
            1 -> ItemMessageSystemBinding::class.java
            2 -> ItemMessageActivityBinding::class.java
            3 -> ItemMessageMatchBinding::class.java
            else -> ItemMessageWalletBinding::class.java
        }
        return clz.inflateMethod?.invoke(null, inflater) as ViewBinding
    }

    @SuppressLint("SimpleDateFormat")
    private fun getTime(timestamp: Long): String {
        val date = Date(timestamp)
        val sdf = SimpleDateFormat("MM-dd HH:mm")
        return sdf.format(date)
    }

    override fun createViewHolder(binding: ViewBinding, viewType: Int): BaseViewHolder {
        val params = RecyclerView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        binding.root.layoutParams = params
        return BaseViewHolder(binding)
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).type
    }

    fun setOnItemClickListener(listener: OnClickListener) {
        clicklistener = listener
    }

    interface OnClickListener {
        fun onDelete(id: Long)
        fun onDetail(id: Long)
    }
}