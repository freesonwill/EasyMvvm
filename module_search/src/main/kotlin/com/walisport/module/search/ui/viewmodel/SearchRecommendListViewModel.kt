package com.walisport.module.search.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.remote.ApiFailedState
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.search.data.repo.SearchRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import plugin.koin.KoinViewModel

@KoinViewModel
class SearchRecommendListViewModel: BaseViewModel() {
    private val repository: SearchRepository by inject { parametersOf(viewModelScope) }

    /** 推薦搜尋關鍵字列表 */
    private val _searchRecommendList = MutableSharedFlow<List<String>>()
    val searchRecommendList: SharedFlow<List<String>> = _searchRecommendList.asSharedFlow()

    /** 取得推薦關鍵字結果 */
    fun getSearchRecommendList(keyword: String? = "", failedHandler: ((error: ApiFailedState?) -> Unit)? = null) {
        callApi(
            { repository.getSearchRecommend(keyword) },
            { state ->
                when (state) {
                    is ApiResponseState.Succeeded<*> -> {
                        viewModelScope.launch {
                            if (state.data is List<*>) {
                                _searchRecommendList.emit(
                                    (state.data as List<*>).filterIsInstance<String>()
                                )
                            }
                        }
                    }
                    is ApiResponseState.Failed -> {
                        failedHandler?.invoke(state.error)
                    }
                    else -> Unit
                }
            },
            false
        )
    }

    /** 清除推薦關鍵字結果 */
    fun clearSearchRecommendList() {
        viewModelScope.launch {
            _searchRecommendList.emit(emptyList())
        }
    }
}