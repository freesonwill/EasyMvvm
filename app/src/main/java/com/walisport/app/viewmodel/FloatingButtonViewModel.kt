package com.walisport.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.walisport.lib_base.data.viewmodel.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FloatingButtonViewModel : BaseViewModel() {

    private val _onBettingCount = MutableLiveData(0)
    val onBettingCount: LiveData<Int> = _onBettingCount

    init {
        // TODO 測試用，須改成監聽下注list size
        viewModelScope.launch(Dispatchers.IO) {
            var count = 0
            while (true) {
                delay(3_000L)
                _onBettingCount.postValue(++count)
            }
        }
    }
}