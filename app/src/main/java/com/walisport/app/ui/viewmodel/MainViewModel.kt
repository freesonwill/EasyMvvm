package com.walisport.app.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.common.data.constants.SkinType
import arch.cayenne.lib.common.ui.viewmodel.BaseActivityViewModel
import com.walisport.app.data.repo.MainRepository
import galaxy.common.proto.Common
import org.koin.core.parameter.parametersOf
import org.koin.core.component.inject

/**
 * @author: zhangsan
 * @date: 2025/3/14 16:56
 * @description:
 */
class MainViewModel : BaseActivityViewModel() {
    private val repository: MainRepository by inject { parametersOf(viewModelScope) }

    init {
        repository.updateSettingReq()
        repository.loadSportList()
    }

    //UI界面上有6种主题，但是逻辑上暂时就白蓝和经典两种
    fun getSkinType(): String {
        val skinType = repository.getSkinType()
        return getLogicSkinType(skinType)
    }

    private fun getLogicSkinType(skinType: String): String {
        return when (skinType) {
            SkinType.SKIN_WHITE_BLUE.value -> SkinType.SKIN_WHITE_BLUE.value
            SkinType.SKIN_WHITE_GREEN.value -> SkinType.SKIN_WHITE_BLUE.value
            else -> SkinType.SKIN_CLASSIC.value
        }
    }
}