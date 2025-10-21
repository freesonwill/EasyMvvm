package com.walisport.module.topup.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.topup.data.entity.BetDetailBean

class BetDetailViewModel : BaseViewModel() {

    private val _onBetDetailListener = MutableLiveData<List<BetDetailBean>>()
    val onBetDetailListener: LiveData<List<BetDetailBean>> get() = _onBetDetailListener

    fun getBetDetailList() {
        val tmp1 = BetDetailBean(0, 0, 0, 0, "0.00", "500.00", "1:00", 1760955736000)
        val tmp2 = BetDetailBean(1, 1, 0, 50, "0.00", "200.00", "1:00", 1760955936000)
        val tmp3 = BetDetailBean(2, 2, 0, 100, "0.00", "2000.00", "1:00", 1760956936000)
        _onBetDetailListener.value = listOf(tmp1, tmp2, tmp3)
    }
}