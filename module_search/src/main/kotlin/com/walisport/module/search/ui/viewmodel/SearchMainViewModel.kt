package com.walisport.module.search.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.search.data.repo.SearchRepository
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import plugin.koin.KoinViewModel

@KoinViewModel
class SearchMainViewModel: BaseViewModel() {
    private val repository: SearchRepository by inject { parametersOf(viewModelScope) }

    /** 歷史搜尋紀錄 */
    private val _recordList = MutableLiveData<List<String>>()
    val searchRecord: LiveData<List<String>> = _recordList

    /** 熱門搜尋關鍵字 */
    private val _searchHotWord = MutableLiveData<List<String>>()
    val searchHotWord: LiveData<List<String>> = _searchHotWord

    /** 以 UID 取得搜尋紀錄 */
    fun getRecordByUID() {
        viewModelScope.launch {
            _recordList.value = repository.getRecordByUID()
        }
    }

    /** 移除所有搜尋紀錄 */
    fun deleteAllData() {
        viewModelScope.launch {
            repository.deleteAllData()
        }
    }

    /** 刪除一筆搜尋紀錄 */
    fun deleteOneRecord(keyword: String?) {
        viewModelScope.launch {
            repository.deleteOneRecord(keyword)
        }
    }

    /** 取得熱門搜尋關鍵字 */
    fun getSearchHotWord() {
        viewModelScope.launch {
            _searchHotWord.value = repository.getSearchHotWord()
        }
    }
}