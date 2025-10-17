package com.walisport.module.me.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.me.data.MeRepository
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import plugin.koin.KoinViewModel

@KoinViewModel
class BottomViewModel : BaseViewModel() {

    private val repository: MeRepository by inject { parametersOf(viewModelScope) }

    override fun initViewModel() {
        super.initViewModel()
    }

    fun createObserver() {

    }
}