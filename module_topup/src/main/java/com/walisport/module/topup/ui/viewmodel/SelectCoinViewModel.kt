package com.walisport.module.topup.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.common.R
import com.walisport.module.topup.data.entity.CoinBean

class SelectCoinViewModel : BaseViewModel() {

    private val _coinList = MutableLiveData<List<CoinBean>>()
    val coinList: LiveData<List<CoinBean>> = _coinList

    fun getCoinList() {
        val tmp0 = CoinBean(0, "USDT", R.drawable.ic_usdt, true)
        val tmp1 = CoinBean(1, "BTC", R.drawable.ic_btc, false)
        val tmp2 = CoinBean(2, "ETH", R.drawable.ic_eth, false)
        val tmp3 = CoinBean(3, "USDT", R.drawable.ic_usdt, false)
        val tmp4 = CoinBean(4, "BTC", R.drawable.ic_btc, false)
        val tmp5 = CoinBean(5, "ETH", R.drawable.ic_eth, false)
        _coinList.value = listOf(tmp0, tmp1, tmp2, tmp3, tmp4, tmp5)
    }

    fun selectCoin(id: Int) {
        val list = _coinList.value!!.toMutableList()
        list.forEach { item ->
            if (item.id == id) {
                item.isSelect = !item.isSelect
            }
        }
        _coinList.value = list
    }
}