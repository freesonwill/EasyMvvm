package com.walisport.module.search.ui.viewmodel

import android.content.Context
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.search.data.model.SearchResultBaseBean
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import plugin.koin.KoinViewModel

@KoinViewModel
class SearchResultListViewModel: BaseViewModel() {
    private val _listData = MutableStateFlow<List<SearchResultBaseBean>>(emptyList())
    val listData: StateFlow<List<SearchResultBaseBean>> = _listData.asStateFlow()

    fun setData(context: Context, data: List<SearchResultBaseBean>) {
        _listData.value = data
    }
}