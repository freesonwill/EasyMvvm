package com.walisport.module.search.data.constants

sealed class SearchResultUiState(type: SearchResultTypeEnum?) : SearchResultBaseUiState(type) {
    data object Loading : SearchResultUiState(null)
    data object Empty : SearchResultUiState(SearchResultTypeEnum.NONE)
    data object ResultList : SearchResultUiState(SearchResultTypeEnum.LIST)
    data class DirectMatch(override val type: SearchResultTypeEnum) : SearchResultUiState(type)
}

open class SearchResultBaseUiState(
    open val type: SearchResultTypeEnum? = null,
)