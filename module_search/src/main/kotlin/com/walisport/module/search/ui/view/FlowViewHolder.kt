package com.walisport.module.search.ui.view

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewbinding.ViewBinding

/**
 * FlowAdapter 專用的基礎 ViewHolder，封裝 ViewBinding 實例
 *
 * @param binding 對應項目的 ViewBinding 物件
 */
open class FlowViewHolder(val binding: ViewBinding) : ViewHolder(binding.root)