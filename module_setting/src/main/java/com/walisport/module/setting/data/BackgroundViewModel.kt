package com.walisport.module.setting.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.walisport.lib.base.data.viewmodel.BaseViewModel
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class BackgroundViewModel : BaseViewModel() {

    private val repository: SettingRepository by inject { parametersOf(viewModelScope) }
    val skinType = MutableLiveData("")

    //设置皮肤背景
    fun setSkinType(type: String) {
        repository.setSkinType(type)
        skinType.value = type
    }
}