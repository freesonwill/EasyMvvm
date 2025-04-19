package com.walisport.module.live.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.viewmodel.BaseViewModel
import arch.cayenne.lib.common.helper.ToastHelper
import com.walisport.module.live.data.LiveBetRepository
import com.walisport.module.live.data.model.LiveBetSlipEnum
import galaxy.common.proto.Common
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf


class LiveBetSlipViewModel : BaseViewModel() {
    private var matchId: Long = -1
    private var sportId: Int = -1
    private var page = 1
    private var pageSize = 10
    private val repository: LiveBetRepository by inject { parametersOf(viewModelScope) }
    private val _orderLiveData = MutableLiveData<List<Common.Order>?>()
    val orderLiveData: LiveData<List<Common.Order>?> = _orderLiveData
    private val _reserveLiveData = MutableLiveData<List<Common.ReserveOrder>?>()
    val reserveLiveData: LiveData<List<Common.ReserveOrder>?> = _reserveLiveData

    fun setIds(matchId: Long, sportId: Int) {
        this.matchId = matchId
        this.sportId = sportId
    }

    fun getOrders(status: LiveBetSlipEnum) {
        viewModelScope.launch {
            val result = repository.getOrderReq(status.value, page, pageSize, sportId, matchId)
            _orderLiveData.value = result
        }
    }

    fun getReserveOrder() {
        viewModelScope.launch {
            val result = repository.getReserveOrder(sportId, matchId)
            _reserveLiveData.value = result
        }

    }


}