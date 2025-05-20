package com.walisport.module.search.viewmodel

import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.search.data.SearchRepository
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import plugin.koin.KoinViewModel

@KoinViewModel
class SearchViewModel : BaseViewModel() {

    private val repository: SearchRepository by inject { parametersOf(viewModelScope) }

    fun getRecordByUID(): List<String> {
        return repository.getRecordByUID()
    }

    fun deleteAllData() {
        repository.deleteAllData()
    }

    fun addOneRecord(key: String) {
        repository.addOneRecord(key)
    }

    fun deleteOneRecord(pos: Int) {
        repository.deleteOneRecord(pos)
    }
}