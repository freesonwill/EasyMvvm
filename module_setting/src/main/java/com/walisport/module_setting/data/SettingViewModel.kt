package com.walisport.module_setting.data

import androidx.lifecycle.viewModelScope
import com.walisport.lib_base.data.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class SettingViewModel : BaseViewModel() {

    private val repository: SettingRepository by inject { parametersOf(viewModelScope) }

    val skinType: StateFlow<String?> get() = repository.skinType

    //设置皮肤背景
    fun setSkinType(type: String) {
        repository.setSkinType(type)
    }
}