package com.walisport.module.topup.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.database.entity.CoinBean
import com.walisport.module.topup.data.TopUpMainRepository

class CryptoViewModel(private val repo: TopUpMainRepository) : BaseViewModel() {

    private val _coinData = MutableLiveData<List<CoinBean>>()
    val coinData: LiveData<List<CoinBean>> = _coinData

    fun getCoinList() {
    }
}