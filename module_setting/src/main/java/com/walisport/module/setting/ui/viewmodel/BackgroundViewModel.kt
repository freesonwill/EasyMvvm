package com.walisport.module.setting.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.common.data.constants.SkinType
import arch.cayenne.lib.skin.SkinnableManager
import com.walisport.module.setting.data.SettingRepository
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import plugin.koin.KoinViewModel

@KoinViewModel
class BackgroundViewModel : BaseViewModel() {

    private val repository: SettingRepository by inject { parametersOf(viewModelScope) }
    private val skinManager: SkinnableManager by inject { parametersOf(viewModelScope) }
    val skinType = MutableLiveData("")

    //设置皮肤背景，这个方法只换肤不写入记录
    fun setSkinType(type: String) {
        viewModelScope.launch {
            val logicSkin = getLogicSkinType(type)
            skinManager.loadSkin(logicSkin)
            skinType.value = type
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

    //UI界面上有6种主题，但是逻辑上暂时就白蓝和经典两种
    private fun getLogicSkinType(skinType: String): String {
        return when (skinType) {
            SkinType.SKIN_WHITE_BLUE.value -> SkinType.SKIN_WHITE_BLUE.value
            SkinType.SKIN_WHITE_GREEN.value -> SkinType.SKIN_WHITE_BLUE.value
            else -> SkinType.SKIN_CLASSIC.value
        }
    }
}