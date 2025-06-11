package com.walisport.module.search.data.constants

import com.walisport.module.search.data.model.SearchResultBean

sealed class SearchResultUiState(type: SearchResultTypeEnum?, data: SearchResultBean? = null) : SearchResultBaseUiState(type) {
    data object Loading : SearchResultUiState(null)
    data object Empty : SearchResultUiState(SearchResultTypeEnum.NONE)
    data class ResultList(override val data: SearchResultBean) : SearchResultUiState(SearchResultTypeEnum.LIST, data)
    data class DirectMatch(override val type: SearchResultTypeEnum, override val data: SearchResultBean) : SearchResultUiState(type, data)
}

open class SearchResultBaseUiState(
    open val type: SearchResultTypeEnum? = null,
    open val data: SearchResultBean? = null,
)