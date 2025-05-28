package com.walisport.module.search.data.constants

import com.walisport.module.search.data.model.SearchDailyMatchBean
import com.walisport.module.search.data.model.SearchMatchBean
import com.walisport.module.search.data.model.SearchResultBaseBean

sealed class SearchResultUiState(type: SearchResultTypeEnum?) : SearchResultBaseUiState(type) {
    data object Loading : SearchResultUiState(null)
    data object Empty : SearchResultUiState(SearchResultTypeEnum.NONE)
    data object ResultList : SearchResultUiState(SearchResultTypeEnum.LIST)
    data class DirectMatch(override val type: SearchResultTypeEnum) : SearchResultUiState(type)
}

open class SearchResultBaseUiState(
    open val type: SearchResultTypeEnum? = null,
)