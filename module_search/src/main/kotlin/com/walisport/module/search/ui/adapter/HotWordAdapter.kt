package com.walisport.module.search.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.search.databinding.ItemHotWordBinding
import com.walisport.module.search.ui.compare.HotWordCompare

class HotWordAdapter(private val onClick: (String) -> Unit) :
    BaseAdapter<String, BaseViewHolder, ViewBinding>(
        HotWordCompare()
    ) {
    override fun convertPlus(holder: BaseViewHolder, binding: ViewBinding, position: Int) {
        val hotWord = getItem(position)
        if (binding is ItemHotWordBinding) {
            with(binding) {
                if (position < 2) {
                    with(tvHotWord) {
                        tvHotWordNormal.visibility = View.GONE
                        visibility = View.VISIBLE
                        text = hotWord
                    }
                } else {
                    with(tvHotWordNormal) {
                        tvHotWord.visibility = View.GONE
                        visibility = View.VISIBLE
                        text = hotWord
                    }
                }
            }
            holder.itemView.clickNoRepeat {
                Toast.makeText(
                    holder.itemView.context,
                    hotWord,
                    Toast.LENGTH_SHORT
                ).show()
                onClick(hotWord)
            }
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ViewBinding {
        return ItemHotWordBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(binding: ViewBinding, viewType: Int): BaseViewHolder {
        return BaseViewHolder(binding)
    }
}