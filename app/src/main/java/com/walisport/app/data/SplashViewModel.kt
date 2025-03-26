package com.walisport.app.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.walisport.lib_base.data.viewmodel.BaseViewModel
import kotlinx.coroutines.launch

class SplashViewModel(private val repository: SplashRepository) : BaseViewModel() {

    val homeTimeSeconds: MutableLiveData<Int> = MutableLiveData()

    init {
        viewModelScope.launch {
            repository.countDownSecondsLD.collect {
                homeTimeSeconds.value = it
            }
        }
    }

}