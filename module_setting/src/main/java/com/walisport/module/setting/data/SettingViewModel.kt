package com.walisport.module.setting.data

import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.viewmodel.BaseViewModel
import arch.cayenne.lib.skin.SportSkinManager
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class SettingViewModel : BaseViewModel() {

    private val repository: SettingRepository by inject { parametersOf(viewModelScope) }
    private val skinManager: SportSkinManager by inject { parametersOf(viewModelScope) }

    //加载皮肤方案
    fun loadMyAppSkin() {
        viewModelScope.launch {
            val skinType = repository.getSkinType()
            if (SkinType.SKIN_CLASSIC.value == skinType) {
                skinManager.loadSkin("classic")
            } else if (SkinType.SKIN_BLACK_BLUE.value == skinType) {
                skinManager.loadSkin("black_blue")
            } else if (SkinType.SKIN_BLACK_GREEN.value == skinType) {
                skinManager.loadSkin("black_green")
            } else if (SkinType.SKIN_BLACK_RED.value == skinType) {
                skinManager.loadSkin("black_red")
            } else if (SkinType.SKIN_WHITE_BLUE.value == skinType) {
                skinManager.loadSkin("white_blue")
            } else if (SkinType.SKIN_WHITE_GREEN.value == skinType) {
                skinManager.loadSkin("white_green")
            }
        }
    }

    //设置赔率显示方式
    fun setDisplayType(type: String) {
        repository.setOddsDisplayType(type)
    }

    //获取赔率显示方式
    fun getDisplayType(): String {
        return repository.getOddsDisplayType()
    }

    //获取语言类型
    fun getLanguageType(): String{
        return repository.getLanguageType()
    }
}