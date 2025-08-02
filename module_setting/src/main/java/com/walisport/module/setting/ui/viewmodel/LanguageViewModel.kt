package com.walisport.module.setting.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.remote.ApiFailedState
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.common.data.constants.LanguageType
import com.walisport.module.setting.data.LanguageRepository
import kotlinx.coroutines.launch

class LanguageViewModel(private val repo: LanguageRepository): BaseViewModel() {

    private val _languageType = MutableLiveData<LanguageType>()
    val languageType: LiveData<LanguageType> get() = _languageType

    private lateinit var firstLanguageType: LanguageType

    var forceUpdate = false
        private set

    init {
        viewModelScope.launch {
            launch {
                val lang = repo.getLanguageType()
                _languageType.value = lang
                firstLanguageType = lang
            }
            launch {
                repo.observeComboBetCount().collect {
                    forceUpdate = it > 0
                }
            }
        }
    }

    //设置语言类型
    fun setLanguageType(type: LanguageType) {
        _languageType.value = type
        if (forceUpdate) {
            saveLanguageType()
        }
    }

    fun saveLanguageType() {
        _languageType.value?.let { type ->
            callApi({
                repo.saveLanguageType(type)
            }, {
                if (it is ApiFailedState) {
                    setLanguageType(firstLanguageType)
                }
            })
        }
    }
}