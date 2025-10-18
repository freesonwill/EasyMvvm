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
class MeVIPInfoViewModel : BaseViewModel() {

    private val repository: MeRepository by inject { parametersOf(viewModelScope) }

    private val _vipLevelLiveData = MutableLiveData<Long>(75)
    val vipLevelLiveData: LiveData<Long> = _vipLevelLiveData

    override fun initViewModel() {
        super.initViewModel()
    }

    fun createObserver() {
        viewModelScope.launch {
            delay(1000)
            _vipLevelLiveData.value = 75
        }
    }
}