package com.walisport.module.me.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.me.data.MeRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import plugin.koin.KoinViewModel

@KoinViewModel
class MeViewModel : BaseViewModel() {
    private val repository: MeRepository by inject { parametersOf(viewModelScope) }
    val bottomIndexFlow:MutableSharedFlow<Int> = MutableSharedFlow(replay = 1)
}