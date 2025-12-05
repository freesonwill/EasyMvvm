package com.walisport.module.topup.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.loge
import arch.cayenne.lib.database.entity.CoinBean
import com.walisport.module.topup.data.TopUpMainRepository
import kotlinx.coroutines.launch

class TopUpViewModel(private val repo: TopUpMainRepository) : BaseViewModel() {

    private val _coinData = MutableLiveData<List<CoinBean>>()
    val coinData: LiveData<List<CoinBean>> = _coinData

}