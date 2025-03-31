package com.walisport.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.walisport.app.repo.FloatingButtonRepository
import com.walisport.lib_base.data.viewmodel.BaseViewModel
import kotlinx.coroutines.launch

class FloatingButtonViewModel(private val repo: FloatingButtonRepository) : BaseViewModel() {

    private val _onBettingCount = MutableLiveData(0)
    val onBettingCount: LiveData<Int> = _onBettingCount

    init {
        viewModelScope.launch {
            repo.getBettingCount().collect {
                _onBettingCount.value = it
            }
        }
    }
}