package com.walisport.module.live.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import com.walisport.module.live.data.model.KeyBoardTabData
import com.walisport.module.live.databinding.ItemKeyboardTabLayoutBinding

class KeyBoardTabAdapter(compare: DiffUtil.ItemCallback<KeyBoardTabData>) :
    BaseAdapter<KeyBoardTabData, KeyBoardTabAdapter.KeyBoardTabViewHolder, ViewBinding>(compare) {
    var selected: Int = 0

    class KeyBoardTabViewHolder(binding: ViewBinding) : BaseViewHolder(binding) {
        val nBinding = binding as ItemKeyboardTabLayoutBinding

        fun update(position: Int, resId: Int) {
            nBinding.iv.tag = position
            nBinding.iv.setImageResource(resId)
        }
    }

    override fun convertPlus(holder: KeyBoardTabViewHolder, binding: ViewBinding, position: Int) {
        holder.update(
            position,
            if (selected == position) getItem(position).select else getItem(position).normal
        )
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ViewBinding {

        val binding = ItemKeyboardTabLayoutBinding.inflate(inflater, parent, false)
        return binding
    }

    override fun createViewHolder(binding: ViewBinding, viewType: Int): KeyBoardTabViewHolder {
        val holder = KeyBoardTabViewHolder(binding)
        holder.nBinding.iv.setOnClickListener {
            val position = it.tag as Int
            selectedItem(position)
        }
        return holder
    }

    private fun selectedItem(position: Int) {
        val lastSelect = selected
        selected = position
        notifyItemChanged(lastSelect)
        notifyItemChanged(selected)
    }


}