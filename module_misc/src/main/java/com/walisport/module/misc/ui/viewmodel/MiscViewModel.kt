package com.walisport.module.misc.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.misc.data.MiscRepository
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import plugin.koin.KoinViewModel

@KoinViewModel
class MiscViewModel : BaseViewModel() {

    private val repository: MiscRepository by inject { parametersOf(viewModelScope) }

}