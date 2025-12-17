package com.walisport.module.hall.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.hall.data.HallRepository
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import plugin.koin.KoinViewModel

@KoinViewModel
class GameFavouriteViewModel : BaseViewModel() {

    private val repository: HallRepository by inject { parametersOf(viewModelScope) }
}