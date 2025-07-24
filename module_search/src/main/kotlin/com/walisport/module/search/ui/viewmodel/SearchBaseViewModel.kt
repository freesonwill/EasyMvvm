package com.walisport.module.search.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.remote.ApiFailedState
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.skin.LanguageManager
import com.walisport.module.search.data.repo.SearchRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import plugin.koin.KoinViewModel
import java.util.Locale

@KoinViewModel
class SearchBaseViewModel: BaseViewModel() {

    private val repository: SearchRepository by inject { parametersOf(viewModelScope) }
    private val languageManager: LanguageManager by inject { parametersOf(viewModelScope) }

    /** 當前語系 */
    private val _currentLanguage = MutableSharedFlow<Locale>(replay = 1)
    val currentLanguage: SharedFlow<Locale> = _currentLanguage.asSharedFlow()

    /** 推薦搜尋關鍵字列表 */
    private val _searchRecommendList = MutableSharedFlow<List<String>>()
    val searchRecommendList: SharedFlow<List<String>> = _searchRecommendList.asSharedFlow()

    /** 搜尋關鍵字 */
    private val _searchKeyWord = MutableSharedFlow<String>()
    val searchKeyWord: SharedFlow<String> = _searchKeyWord.asSharedFlow()

    /** 日期選擇器開啟狀態 */
    private val _isDatePickerOpen = MutableSharedFlow<Boolean>(replay = 1)
    val isDatePickerOpen: SharedFlow<Boolean> = _isDatePickerOpen.asSharedFlow()

    init {
        // 初始化當前語系為預設語系
        _currentLanguage.tryEmit(Locale.getDefault())

        // 初始化日期選擇器開啟狀態
        _isDatePickerOpen.tryEmit(false)

        // 監聽語系變化
        viewModelScope.launch {
            languageManager.languageFlow.collect {
                setCurrentLanguage(it ?: Locale.getDefault())
            }
        }
    }

    /** 設置當前語系 */
    private fun setCurrentLanguage(locale: Locale) {
        viewModelScope.launch {
            _currentLanguage.emit(locale)
        }
    }

    /** 取得當前語系 */
    fun getCurrentLanguage(): Locale {
        return _currentLanguage.replayCache.firstOrNull() ?: Locale.getDefault()
    }

    /** 新增一筆搜尋紀錄 */
    fun addOneRecord(key: String) {
        viewModelScope.launch {
            repository.addOneRecord(key)
        }
    }

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

    /** 設置搜尋關鍵字 */
    fun setSearchKeyWord(key: String) {
        viewModelScope.launch {
            _searchKeyWord.emit(key)
        }
    }

    /** 設置日期選擇器開啟狀態 */
    fun setIsDatePickerOpen(isOpen: Boolean) {
        viewModelScope.launch {
            _isDatePickerOpen.emit(isOpen)
        }
    }

    /** 取得日期選擇器是否開啟 */
    fun isDatePickerOpen(): Boolean {
        return _isDatePickerOpen.replayCache.firstOrNull() ?: false
    }
}