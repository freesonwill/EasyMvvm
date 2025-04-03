package com.walisport.app.data

import androidx.lifecycle.viewModelScope
import com.walisport.lib.base.data.viewmodel.BaseViewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.component.inject

/**
 * @author: zhangsan
 * @date: 2025/3/14 16:56
 * @description:
 */
class MainViewModel : BaseViewModel() {
    private val repository : MainRepository by inject { parametersOf(viewModelScope) }
}