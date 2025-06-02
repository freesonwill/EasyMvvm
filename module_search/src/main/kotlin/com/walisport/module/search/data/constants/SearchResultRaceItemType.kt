package com.walisport.module.search.data.constants

import com.walisport.module.search.data.model.SearchMatchBean

sealed class SearchResultRaceItemType {
    data class Header(val title: String) : SearchResultRaceItemType()
    data class Item(val data: SearchMatchBean) : SearchResultRaceItemType()
}