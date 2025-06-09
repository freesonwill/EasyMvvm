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
class SearchViewModel : BaseViewModel() {
    private val repository: SearchRepository by inject { parametersOf(viewModelScope) }

    /** 推薦搜尋關鍵字 */
    private val _searchRecommend = MutableLiveData<List<String>>()
    val searchRecommend: LiveData<List<String>> = _searchRecommend

    /** 新增一筆搜尋紀錄 */
    fun addOneRecord(key: String) {
        viewModelScope.launch {
            repository.addOneRecord(key)
        }
    }

    /** 取得推薦關鍵字結果 */
    fun getSearchRecommend(keyword: String? = "") {
        viewModelScope.launch {
            _searchRecommend.value = repository.getSearchRecommend(keyword)
        }
    }

    /** 清除推薦關鍵字結果 */
    fun clearSearchRecommend() {
        _searchRecommend.value = emptyList()
    }
}