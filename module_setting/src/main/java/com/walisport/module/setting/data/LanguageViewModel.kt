package com.walisport.module.setting.data

import androidx.lifecycle.viewModelScope
import com.walisport.lib_base.data.viewmodel.BaseViewModel
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class LanguageViewModel : BaseViewModel() {

    private val repository: SettingRepository by inject { parametersOf(viewModelScope) }

    //设置语言类型
    fun setLanguageType(type: String) {
        repository.setLanguageType(type)
    }

    //获取语言类型
    fun getLanguageType(): String {
        return repository.getLanguageType()
    }
}
