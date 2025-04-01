package com.walisport.module_bet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.walisport.lib_base.data.viewmodel.BaseViewModel
import com.walisport.module_bet.repo.FloatingButtonRepository
import kotlinx.coroutines.launch

class FloatingButtonViewModel(private val repo: FloatingButtonRepository) : BaseViewModel() {

    private val _onBettingCount = MutableLiveData(0)
    val onBettingCount: LiveData<Int> = _onBettingCount

    init {
        viewModelScope.launch {
            repo.observeBettingCount().collect {
                _onBettingCount.value = it
            }
        }
    }
}