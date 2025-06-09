package com.walisport.module.search.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.search.data.constants.SearchResultTypeEnum
import com.walisport.module.search.data.constants.SearchResultUiState
import com.walisport.module.search.data.model.SearchResultBean
import com.walisport.module.search.data.repo.SearchRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import plugin.koin.KoinViewModel

@KoinViewModel
class SearchResultBaseViewModel: BaseViewModel() {
    private val repository: SearchRepository by inject { parametersOf(viewModelScope) }

    /** 搜尋結果頁 UI 狀態 */
    private val _uiState = MutableSharedFlow<SearchResultUiState>()
    val uiState: SharedFlow<SearchResultUiState> = _uiState.asSharedFlow()

    /** 取得搜尋結果 */
    fun getSearchResult(keyword: String) {
        viewModelScope.launch {
            setUiState(SearchResultUiState.Loading)
            setResult(repository.getSearchResult(keyword))
        }
    }

    /** 處理搜尋結果 */
    private fun setResult(result: SearchResultBean) {
        when (result.type) {
            SearchResultTypeEnum.NONE -> {
                setUiState(SearchResultUiState.Empty)
            }

            SearchResultTypeEnum.LIST -> {
                setUiState(SearchResultUiState.ResultList(result))
            }

            SearchResultTypeEnum.TOURNAMENT,
            SearchResultTypeEnum.TEAM,
            SearchResultTypeEnum.PLAYER -> {
                setUiState(SearchResultUiState.DirectMatch(result.type, result))
            }
        }
    }

    /** 設定搜尋結果頁 UI 狀態 */
    private fun setUiState(state: SearchResultUiState) {
        viewModelScope.launch {
            _uiState.emit(state)
        }
    }
}