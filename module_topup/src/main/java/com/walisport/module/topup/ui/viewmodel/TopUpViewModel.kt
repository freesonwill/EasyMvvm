package com.walisport.module.topup.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.topup.data.TopUpMainRepository
import kotlinx.coroutines.launch

class TopUpViewModel(private val repo: TopUpMainRepository) : BaseViewModel() {

    fun getCurrencyList(){
        viewModelScope.launch {
            repo.getCurrencyList()
        }
    }
}