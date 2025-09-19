package com.walisport.app.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.common.data.constants.SkinType
import arch.cayenne.lib.common.ui.viewmodel.BaseActivityViewModel
import com.walisport.app.data.repo.MainRepository
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

/**
 * @author: zhangsan
 * @date: 2025/3/14 16:56
 * @description:
 */
class MainViewModel : BaseActivityViewModel() {

    private val repository: MainRepository by inject { parametersOf(viewModelScope) }
    override val shouldBeAutoLogin: Boolean = true

    override fun initViewModel() {
        super.initViewModel()
        viewModelScope.launch {
            repository.observeLoginChange()
                .filter { it }
                .collect {
                repository.loadSportList()
                repository.observeSystemNotify()
            }
        }

    }

    //UI界面上有6种主题，但是逻辑上暂时就白蓝和经典两种
    fun getSkinType(): String {
        val skinType = repository.getSkinType()
        return SkinType.getLogicSkinType(skinType)
    }
}