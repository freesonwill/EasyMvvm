package com.walisport.module.live.ui.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.viewbinding.ViewBinding
import com.walisport.lib.common.ui.adapter.BaseAdapter
import com.walisport.lib.common.ui.adapter.BaseViewHolder
import com.walisport.lib.common.utils.ext.DimensionExt.dp2px
import com.walisport.module.live.R
import com.walisport.module.live.databinding.AdapterLiveBetSlipMenuItemLayoutBinding

class LiveBetSlipAdapter(compare: DiffUtil.ItemCallback<String>) :
    BaseAdapter<String, LiveBetSlipAdapter.LiveBetSlipViewHolder, ViewBinding>(
        compare
    ) {
    private var selected: Int = 0

    inner class LiveBetSlipViewHolder(binding: ViewBinding) : BaseViewHolder(binding) {
        private val viewBinding: AdapterLiveBetSlipMenuItemLayoutBinding =
            binding as AdapterLiveBetSlipMenuItemLayoutBinding

        init {
            setOnClickListener()
        }

        private fun setOnClickListener() {
            viewBinding.tvMenu.setOnClickListener {
                val position: Int = it.tag as Int
                updateSelected(position)
            }
        }

        fun updateItem(position: Int) {
            viewBinding.tvMenu.tag = position
            viewBinding.tvMenu.setBackgroundResource(if (selected == position) R.drawable.shape_betslip_selected else R.drawable.shape_betslip_normal)
            viewBinding.tvMenu.setTextColor(
                ContextCompat.getColor(
                    viewBinding.tvMenu.context,
                    if (selected == position) R.color.theme_text else R.color.secondary_text
                )
            )
            viewBinding.tvMenu.text = getItem(position)
            viewBinding.tvMenu.width = if (position <= 2) 72.dp2px else 60.dp2px
        }

    }


    @SuppressLint("NotifyDataSetChanged")
    private fun updateSelected(value: Int) {
        val lastSelected = selected
        if (selected == value) {
            return
        }
        selected = value
        notifyItemChanged(lastSelected)
        notifyItemChanged(value)
        Log.i("aaa", "lastSelected $lastSelected   value $value")
    }

    override fun convertPlus(holder: LiveBetSlipViewHolder, binding: ViewBinding, position: Int) {
        holder.updateItem(position)
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ViewBinding {
        val binding = AdapterLiveBetSlipMenuItemLayoutBinding.inflate(inflater)
        return binding
    }

    override fun createViewHolder(
        binding: ViewBinding,
        viewType: Int
    ): LiveBetSlipViewHolder {
        val holder = LiveBetSlipViewHolder(binding)
        return holder
    }
}