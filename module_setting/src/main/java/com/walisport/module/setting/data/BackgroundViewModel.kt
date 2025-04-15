package com.walisport.module.setting.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.viewmodel.BaseViewModel
import arch.cayenne.lib.skin.SportSkinManager
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class BackgroundViewModel : BaseViewModel() {

    private val repository: SettingRepository by inject { parametersOf(viewModelScope) }
    private val skinManager: SportSkinManager by inject { parametersOf(viewModelScope) }

    val skinType = MutableLiveData("")

    //设置皮肤背景，这个方法只换肤不写入记录
    fun setSkinType(type: SkinType) {
        viewModelScope.launch {
            if (SkinType.SKIN_CLASSIC == type) {
                skinManager.loadSkin("classic")
            } else if (SkinType.SKIN_BLACK_BLUE == type) {
                skinManager.loadSkin("black_blue")
            } else if (SkinType.SKIN_BLACK_GREEN == type) {
                skinManager.loadSkin("black_green")
            } else if (SkinType.SKIN_BLACK_RED == type) {
                skinManager.loadSkin("black_red")
            } else if (SkinType.SKIN_WHITE_BLUE == type) {
                skinManager.loadSkin("white_blue")
            } else if (SkinType.SKIN_WHITE_GREEN == type) {
                skinManager.loadSkin("white_green")
            }
        }
        skinType.value = type.value
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