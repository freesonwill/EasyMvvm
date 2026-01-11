package com.walisport.module.search.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.search.data.constants.SearchResultTypeEnum
import com.walisport.module.search.data.constants.SearchResultUiState
import com.walisport.module.search.data.constants.SearchResultUiState.GameCategoryMatch
import com.walisport.module.search.data.constants.SearchResultUiState.VendorDirectMatch
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

    /** 監聽登入狀態變化 */
    fun observeLoginChange() = repository.observeUserLogin()

    /** 搜尋結果頁 UI 狀態 */
    private val _uiState = MutableSharedFlow<SearchResultUiState>()
    val uiState: SharedFlow<SearchResultUiState> = _uiState.asSharedFlow()

    /** 取得搜尋結果 */
    fun getSearchResult(keyword: String) {
        viewModelScope.launch {
            // 1. 先判斷是否為「遊戲供應商精準關鍵字」
            repository.findSupplierByKeyword(keyword)?.let { supplier ->
                setUiState(VendorDirectMatch(keyword, supplier))
                return@launch
            }

            // 2. 判斷是否為「遊戲分類關鍵字」（例如：電子、老虎機）
            repository.matchGameCategoryByKeyword(keyword)?.let { gameTypeId ->
                setUiState(GameCategoryMatch(keyword, gameTypeId))
                return@launch
            }

            // 3. 否則走原本的體育搜尋流程
            callApi(
                { repository.getSearchResult(keyword) },
                { state ->
                    when (state) {
                        is ApiResponseState.Succeeded<*> -> {
                            (state.data as SearchResultBean)
                                .takeIf { it.type != SearchResultTypeEnum.NONE }
                                ?.let { setResult(it) }
                                ?: setState(DataState.DataEmpty)
                        }
                        else -> Unit
                    }
                }
            )
        }
    }

    /** 處理搜尋結果 */
    private fun setResult(result: SearchResultBean) {
        when (result.type) {
            SearchResultTypeEnum.LIST -> {
                setUiState(SearchResultUiState.ResultList(result))
            }

            SearchResultTypeEnum.TOURNAMENT,
            SearchResultTypeEnum.TEAM,
            SearchResultTypeEnum.PLAYER -> {
                setUiState(SearchResultUiState.DirectMatch(result.type, result))
            }
            else -> Unit
        }
    }

    /** 設定搜尋結果頁 UI 狀態 */
    private fun setUiState(state: SearchResultUiState) {
        viewModelScope.launch {
            _uiState.emit(state)
        }
    }
}