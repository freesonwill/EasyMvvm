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

    fun getSkinType(): String {
        return repository.getSkinType()
    }
}