package com.walisport.module.search.ui.compare

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import com.walisport.module.search.data.constants.SearchResultListItemType

class SearchResultGridCompare:DiffUtil.ItemCallback<SearchResultListItemType>() {
    override fun areItemsTheSame(
        oldItem: SearchResultListItemType,
        newItem: SearchResultListItemType
    ): Boolean {
        return when {
            oldItem is SearchResultListItemType.Header && newItem is SearchResultListItemType.Header ->
                oldItem.resId == newItem.resId

            oldItem is SearchResultListItemType.Item && newItem is SearchResultListItemType.Item ->
                oldItem.data.id == newItem.data.id

            else -> false
        }
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: SearchResultListItemType, newItem: SearchResultListItemType): Boolean {
        return oldItem == newItem
    }
}