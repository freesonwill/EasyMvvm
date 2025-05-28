package com.walisport.module.search.ui.viewmodel

import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.search.data.constants.SearchResultTypeEnum
import com.walisport.module.search.data.constants.SearchResultUiState
import com.walisport.module.search.data.model.SearchResultBean
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import plugin.koin.KoinViewModel

@KoinViewModel
class SearchResultViewModel : BaseViewModel() {
    private val _uiState = MutableStateFlow<SearchResultUiState>(SearchResultUiState.Loading)
    val uiState: StateFlow<SearchResultUiState> = _uiState

    fun setResult(result: SearchResultBean) {
        when (result.type) {
            SearchResultTypeEnum.NONE -> {
                _uiState.value = SearchResultUiState.Empty
            }

            SearchResultTypeEnum.LIST -> {
                _uiState.value = SearchResultUiState.ResultList(result.dataList ?: emptyList())
            }

            SearchResultTypeEnum.TOURNAMENT,
            SearchResultTypeEnum.TEAM,
            SearchResultTypeEnum.PLAYER -> {
                _uiState.value = SearchResultUiState.DirectMatch(
                    type = result.type,
                    directData = result.directData
                        ?: throw IllegalArgumentException("Direct data cannot be null for type ${result.type}"),
                    matchTotal = result.matchTotal ?: 0,
                    matches = result.matches ?: emptyList(),
                    dailyCount = result.dailyCount ?: emptyList()
                )
            }
        }
    }
}