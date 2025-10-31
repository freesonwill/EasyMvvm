package com.walisport.module.topup.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.common.R
import com.walisport.module.topup.data.entity.CoinBean

class CryptoViewModel : BaseViewModel() {

    private val _coinData = MutableLiveData<List<CoinBean>>()
    val coinData: LiveData<List<CoinBean>> = _coinData

    fun getCoinList() {
        val tmp1 = CoinBean(0, "USDT", R.drawable.ic_usdt,false)
        val tmp2 = CoinBean(1, "ETH", R.drawable.ic_eth,false)
        val tmp3 = CoinBean(2, "BTC", R.drawable.ic_btc,false)
        val tmp4 = CoinBean(3, "LTC", R.drawable.ic_usdt,false)
        val tmp5 = CoinBean(4, "DOG", R.drawable.ic_btc,false)
        val tmp6 = CoinBean(5, "ONE", R.drawable.ic_eth,false)
        _coinData.value = listOf(tmp1, tmp2, tmp3, tmp4, tmp5, tmp6)
    }
}