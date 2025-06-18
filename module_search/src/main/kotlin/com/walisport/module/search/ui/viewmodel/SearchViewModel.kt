package com.walisport.module.search.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.skin.LanguageManager
import arch.cayenne.lib.skin.SkinnableManager
import com.walisport.module.search.data.constants.SearchNavigationEvent
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
class SearchViewModel : BaseViewModel() {

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

    /** 導航事件 */
    private val _navigateEvent = MutableSharedFlow<SearchNavigationEvent>()
    val navigationEvent: SharedFlow<SearchNavigationEvent> = _navigateEvent.asSharedFlow()

    /** 結果頁背景顏色 */
    private val _resultBackgroundColor = MutableSharedFlow<Int?>()
    val resultBackgroundColor: SharedFlow<Int?> = _resultBackgroundColor.asSharedFlow()

    /** 狀態欄狀態 */
    private val _statusBarState = MutableSharedFlow<Boolean>(replay = 1)
    val statusBarState: SharedFlow<Boolean> = _statusBarState.asSharedFlow()

    /** 標題欄遮罩狀態 */
    private val _titleBarMaskEvent = MutableSharedFlow<Pair<Boolean, (() -> Unit)?>>()
    val titleBarMaskEvent: SharedFlow<Pair<Boolean, (() -> Unit)?>> = _titleBarMaskEvent

    init {
        // 初始化當前語系為預設語系
        _currentLanguage.tryEmit(Locale.getDefault())

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
    fun getSearchRecommendList(keyword: String? = "") {
        viewModelScope.launch {
            _searchRecommendList.emit(repository.getSearchRecommend(keyword))
        }
    }

    /** 清除推薦關鍵字結果 */
    fun clearSearchRecommendList() {
        viewModelScope.launch {
            _searchRecommendList.emit(emptyList())
        }
    }

    /** 設置導航事件 */
    fun setNavigationEvent(event: SearchNavigationEvent) {
        viewModelScope.launch {
            _navigateEvent.emit(event)
        }
    }

    /** 設置搜尋關鍵字 */
    fun setSearchKeyWord(key: String) {
        viewModelScope.launch {
            _searchKeyWord.emit(key)
        }
    }

    /** 設置結果背景顏色 */
    fun setResultBackgroundColor(color: Int?) {
        viewModelScope.launch {
            _resultBackgroundColor.emit(color)
        }
    }

    /** 設置狀態欄狀態 */
    fun setStatusBarState(isDefault: Boolean) {
        viewModelScope.launch {
            _statusBarState.emit(isDefault)
        }
    }

    /** 設置標題欄遮罩狀態 */
    fun setTitleBarMaskEvent(isEnabled: Boolean, onClick: (() -> Unit)? = null) {
        viewModelScope.launch {
            _titleBarMaskEvent.emit(Pair(isEnabled, onClick))
        }
    }
}