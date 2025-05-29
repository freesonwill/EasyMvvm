package com.walisport.module.search.data.constants

import com.walisport.module.search.data.model.SearchResultBaseBean

sealed class SearchResultListItemType {
    data class Header(val title: String) : SearchResultListItemType()
    data class Item(val data: SearchResultBaseBean) : SearchResultListItemType()
    data class More(val type: SearchResultTypeEnum) : SearchResultListItemType()
}