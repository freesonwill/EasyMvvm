package com.walisport.lib.common.ui.adapter

import android.content.res.Resources
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

/***
 * @param binding: ViewBinding
 *
 * @author Link Hsieh
 */
open class BaseViewHolder(val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {

    fun getString(id: Int) = itemView.resources.getString(id)
    val resources: Resources
        get() = itemView.resources

    companion object {
        const val ITEM_HEADER = 0
        const val ITEM_BODY = 1
        const val ITEM_FOOTER = 2
    }
}