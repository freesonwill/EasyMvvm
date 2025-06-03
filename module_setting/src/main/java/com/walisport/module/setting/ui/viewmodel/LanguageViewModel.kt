package com.walisport.module.setting.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.skin.SkinnableManager
import com.walisport.module.setting.data.LanguageType
import com.walisport.module.setting.data.SettingRepository
import galaxy.common.proto.Common
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import plugin.koin.KoinViewModel
import java.util.Locale

@KoinViewModel
class LanguageViewModel : BaseViewModel() {

    private val repository: SettingRepository by inject { parametersOf(viewModelScope) }
    private val skinManager: SkinnableManager by inject { parametersOf(viewModelScope) }
    val languageType = MutableLiveData("")

    //设置语言类型
    fun setLanguageType(type: LanguageType) {
        viewModelScope.launch {
            when (type) {
                LanguageType.LANGUAGE_ENGLISH -> {
                    skinManager.changeLanguage(Locale.US)
                }

                LanguageType.LANGUAGE_SIMPLE -> {
                    skinManager.changeLanguage(Locale.SIMPLIFIED_CHINESE)
                }

                LanguageType.LANGUAGE_ID -> {
                    skinManager.changeLanguage(Locale("id"))
                }

                LanguageType.LANGUAGE_PT -> {
                    skinManager.changeLanguage(Locale("pt"))
                }
            }
            languageType.value = type.value
            repository.setLanguageType(type.value)
        }
        updateLanguageSetting(type.value)
    }

    //获取语言类型
    fun getLanguageType(): String {
        return repository.getLanguageType()
    }

    //调用接口设置语言
    private fun updateLanguageSetting(type: String) {
        viewModelScope.launch {
            val req = Common.Setting.newBuilder().apply {
                lang = type
            }.build()
            repository.updateSettingReq(req)
        }
    }
}