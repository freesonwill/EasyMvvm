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

/**
 * 搜索推薦錯誤狀態
 */
sealed class SearchRecommendError {
    /** 超時錯誤 */
    data class Timeout(val message: String) : SearchRecommendError()
    
    /** 一般錯誤 */
    data class General(val error: ApiFailedState) : SearchRecommendError()
    
    /** 無錯誤 */
    object None : SearchRecommendError()
}

@KoinViewModel
class SearchRecommendListViewModel: BaseViewModel() {
    private val repository: SearchRepository by inject { parametersOf(viewModelScope) }

    /** 推薦搜尋關鍵字列表 */
    private val _searchRecommendList = MutableSharedFlow<List<String>>()
    val searchRecommendList: SharedFlow<List<String>> = _searchRecommendList.asSharedFlow()

    /** 錯誤狀態 Flow */
    private val _errorState = MutableSharedFlow<SearchRecommendError>(replay = 0)
    val errorState: SharedFlow<SearchRecommendError> = _errorState.asSharedFlow()

    /** 取得推薦關鍵字結果 */
    fun getSearchRecommendList(keyword: String? = "") {
        viewModelScope.launch {
            // 清除之前的錯誤狀態
            _errorState.emit(SearchRecommendError.None)
            
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
                                } else {
                                    _searchRecommendList.emit(emptyList())
                                }
                            }
                        }
                        is ApiResponseState.Failed -> {
                            viewModelScope.launch {
                                val error = if (isTimeoutError(state.error)) {
                                    SearchRecommendError.Timeout(
                                        state.error?.msg ?: "搜索超时，请重试"
                                    )
                                } else {
                                    SearchRecommendError.General(
                                        state.error ?: object : ApiFailedState {
                                            override val code: Int? = null
                                            override val msg: String = "未知错误"
                                        }
                                    )
                                }
                                _errorState.emit(error)
                            }
                        }
                        else -> Unit
                    }
                },
                false
            )
        }
    }

    /**
     * 判斷是否為超時錯誤
     */
    private fun isTimeoutError(error: ApiFailedState?): Boolean {
        if (error == null) return false
        
        // 檢查錯誤碼（常見超時錯誤碼）
        error.code?.let { code ->
            if (code == 408 || code == -1) return true
        }
        
        // 檢查錯誤訊息（包含超時相關關鍵字）
        val errorMsg = error.msg.lowercase()
        return errorMsg.contains("timeout") || 
               errorMsg.contains("超时") || 
               errorMsg.contains("超時") ||
               errorMsg.contains("timed out")
    }

    /** 清除推薦關鍵字結果 */
    fun clearSearchRecommendList() {
        viewModelScope.launch {
            _searchRecommendList.emit(emptyList())
            _errorState.emit(SearchRecommendError.None)
        }
    }
}