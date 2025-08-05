package com.walisport.module.search.ui.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.viewModelScope
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

    /** 暫存的螢幕截圖 */
    private var tempScreenShot: Bitmap? = null

    /** 當前搜尋關鍵字 */
    private var currentKeyWord: String? = null

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

    /** 設置暫存的螢幕截圖 */
    fun setTempScreenShot(bitmap: Bitmap?) {
        tempScreenShot = bitmap
    }

    /** 取得暫存的螢幕截圖 */
    fun getTempScreenShot(): Bitmap? {
        return tempScreenShot
    }

    /** 清除暫存的螢幕截圖 */
    fun clearTempScreenShot() {
        tempScreenShot?.recycle()
        tempScreenShot = null
    }

    /** 設置當前搜尋關鍵字 */
    fun setCurrentKeyword(keyword: String?) {
        currentKeyWord = keyword
    }

    /** 取得當前搜尋關鍵字 */
    fun getCurrentKeyword(): String? {
        return currentKeyWord
    }
}