package com.walisport.module.topup.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.topup.data.entity.CoinBean

class CryptoViewModel : BaseViewModel() {

    private val _coinData = MutableLiveData<List<CoinBean>>()
    val coinData: LiveData<List<CoinBean>> = _coinData

    fun getCoinList() {
        val tmp1 = CoinBean(0, "USDT", "")
        val tmp2 = CoinBean(0, "ETH", "")
        val tmp3 = CoinBean(0, "BTC", "")
        val tmp4 = CoinBean(0, "LTC", "")
        val tmp5 = CoinBean(0, "DOG", "")
        val tmp6 = CoinBean(0, "ONE", "")
        _coinData.value = listOf(tmp1, tmp2, tmp3, tmp4, tmp5, tmp6)
    }
}