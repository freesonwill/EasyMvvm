package com.walisport.app.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.common.ui.viewmodel.BaseActivityViewModel
import com.walisport.app.data.repo.MainRepository
import org.koin.core.parameter.parametersOf
import org.koin.core.component.inject

/**
 * @author: zhangsan
 * @date: 2025/3/14 16:56
 * @description:
 */
class MainViewModel : BaseActivityViewModel() {
    private val repository : MainRepository by inject { parametersOf(viewModelScope) }

    fun getSkinType():String{
        return repository.getSkinType()
    }
}