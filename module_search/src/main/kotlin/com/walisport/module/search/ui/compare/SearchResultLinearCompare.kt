package com.walisport.module.search.ui.compare

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import com.walisport.module.search.data.constants.SearchResultListItemType
import com.walisport.module.search.data.model.SearchResultBaseBean

class SearchResultLinearCompare:DiffUtil.ItemCallback<SearchResultBaseBean>() {
    override fun areItemsTheSame(
        oldItem: SearchResultBaseBean,
        newItem: SearchResultBaseBean
    ): Boolean {
        return oldItem.id == newItem.id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: SearchResultBaseBean, newItem: SearchResultBaseBean): Boolean {
        return oldItem == newItem
    }
}