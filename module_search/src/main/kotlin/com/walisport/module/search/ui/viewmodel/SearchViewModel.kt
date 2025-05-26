package com.walisport.module.search.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.search.data.repo.SearchRepository
import com.walisport.module.search.data.model.SearchResultBean
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import plugin.koin.KoinViewModel

@KoinViewModel
class SearchViewModel : BaseViewModel() {

    private val repository: SearchRepository by inject { parametersOf(viewModelScope) }
    private val _recordList = MutableLiveData<List<String>>()
    val searchRecord: LiveData<List<String>> = _recordList

    private val _searchHotWord = MutableLiveData<List<String>>()
    val searchHotWord: LiveData<List<String>> = _searchHotWord

    private val _searchRecommend = MutableLiveData<List<String>>()
    val searchRecommend: LiveData<List<String>> = _searchRecommend

    private val _searchResult = MutableLiveData<SearchResultBean>()
    val searchResult: LiveData<SearchResultBean> = _searchResult

    fun getRecordByUID() {
        viewModelScope.launch {
            val res = repository.getRecordByUID()
            _recordList.value = res
        }
    }

    fun deleteAllData() {
        viewModelScope.launch {
            repository.deleteAllData()
        }
    }

    fun addOneRecord(key: String) {
        viewModelScope.launch {
            repository.addOneRecord(key)
        }
    }

    fun deleteOneRecord(keyword: String?) {
        viewModelScope.launch {
            repository.deleteOneRecord(keyword)
        }
    }

    fun getSearchResult(keyword: String) {
        viewModelScope.launch {
            _searchResult.value = repository.getSearchResult(keyword)
        }
    }

    fun getSearchRecommend(keyword: String? = "") {
        viewModelScope.launch {
            val response = repository.getSearchRecommend(keyword)
            _searchRecommend.value = response
        }
    }

    fun clearSearchRecommend() {
        _searchRecommend.value = emptyList()
    }

    fun getSearchHotWord() {
        viewModelScope.launch {
            _searchHotWord.value = repository.getSearchHotWord()
        }
    }
}