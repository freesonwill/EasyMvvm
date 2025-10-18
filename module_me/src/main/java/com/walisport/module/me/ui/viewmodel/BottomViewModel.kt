package com.walisport.module.me.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.me.data.MeRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import plugin.koin.KoinViewModel

@KoinViewModel
class BottomViewModel : BaseViewModel() {

    private val repository: MeRepository by inject { parametersOf(viewModelScope) }

    private val _recentlyCount = MutableLiveData<Long>(0)
    val recentlyCount: LiveData<Long> = _recentlyCount

    private val _gameCount = MutableLiveData<Long>(0)
    val gameCount: LiveData<Long> = _gameCount

    private val _matchCount = MutableLiveData<Long>(0)
    val matchCount: LiveData<Long> = _matchCount

    override fun initViewModel() {
        super.initViewModel()
    }

    fun createObserver() {
        viewModelScope.launch {
            delay(1500)
            _recentlyCount.value = 1000
            _gameCount.value = 1
            _matchCount.value = 1
        }
    }
}