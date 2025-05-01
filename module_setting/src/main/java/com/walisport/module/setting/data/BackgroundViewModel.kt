package com.walisport.module.setting.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.common.data.constants.SkinType
import arch.cayenne.lib.skin.SportSkinManager
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import plugin.koin.KoinViewModel

@KoinViewModel
class BackgroundViewModel : BaseViewModel() {

    private val repository: SettingRepository by inject { parametersOf(viewModelScope) }
    private val skinManager: SportSkinManager by inject { parametersOf(viewModelScope) }

    val skinType = MutableLiveData("")

    //设置皮肤背景，这个方法只换肤不写入记录
    fun setSkinType(type: SkinType) {
        viewModelScope.launch {
        skinManager.loadSkin(type.value)
        skinType.value = type.value
        }
    }

    //点击确认键后才会写入数据，否则只是换肤显示
    fun setSkinData(type: String) {
        repository.setSkinType(type)
    }

    //获取皮肤类型
    fun getSkinData(): String {
        return repository.getSkinType()
    }
}