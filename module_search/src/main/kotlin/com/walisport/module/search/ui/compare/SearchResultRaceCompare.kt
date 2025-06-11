package com.walisport.module.search.ui.compare

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import com.walisport.module.search.data.constants.SearchResultListItemType
import com.walisport.module.search.data.constants.SearchResultRaceItemType

class SearchResultRaceCompare:DiffUtil.ItemCallback<SearchResultRaceItemType>() {
    override fun areItemsTheSame(
        oldItem: SearchResultRaceItemType,
        newItem: SearchResultRaceItemType
    ): Boolean {
        return when {
            oldItem is SearchResultRaceItemType.Header && newItem is SearchResultRaceItemType.Header ->
                oldItem.title == newItem.title

            oldItem is SearchResultRaceItemType.Item && newItem is SearchResultRaceItemType.Item ->
                oldItem.data.matchId == newItem.data.matchId

            else -> false
        }
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: SearchResultRaceItemType, newItem: SearchResultRaceItemType): Boolean {
        return oldItem == newItem
    }
}