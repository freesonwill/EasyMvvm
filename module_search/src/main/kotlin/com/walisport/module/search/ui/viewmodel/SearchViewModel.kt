package com.walisport.module.search.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.search.data.SearchRepository
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import plugin.koin.KoinViewModel

@KoinViewModel
class SearchViewModel : BaseViewModel() {

    private val repository: SearchRepository by inject { parametersOf(viewModelScope) }
    private val _recordList = MutableLiveData<List<String>>()
    val searchRecord: LiveData<List<String>> = _recordList

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
}