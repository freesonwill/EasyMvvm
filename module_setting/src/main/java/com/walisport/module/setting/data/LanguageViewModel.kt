package com.walisport.module.setting.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class LanguageViewModel : BaseViewModel() {

    private val repository: SettingRepository by inject { parametersOf(viewModelScope) }
    val languageType = MutableLiveData("")

    //设置语言类型
    fun setLanguageType(type: LanguageType) {
        languageType.value = type.value
        repository.setLanguageType(type.value)
    }

    //获取语言类型
    fun getLanguageType(): String {
        return repository.getLanguageType()
    }
}