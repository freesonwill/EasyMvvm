package com.walisport.module.live.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.LogUtils
import com.walisport.module.live.data.livebetslip.LiveBetSlipData
import com.walisport.module.live.data.repository.LiveBetRepository
import com.walisport.module.live.data.model.LiveBetSlipEnum
import galaxy.common.proto.Common
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import java.math.BigDecimal


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
            LogUtils.dTag("aaa","gerRerveOrder ${result?.size}")
            _reserveLiveData.value = result
        }

    }


    fun getTestList(): List<LiveBetSlipData> {
        val order = Common.Order.newBuilder().setBetId("0").build()
        val order1 = Common.Order.newBuilder().setBetId("1").build()
        val tmpList = arrayListOf(LiveBetSlipData(order), LiveBetSlipData(order1))
        return tmpList
    }

    fun getTestList1(): List<Common.ReserveOrder> {
        val order = Common.ReserveOrder.newBuilder().setReserveId("0").build()
        val order1 = Common.ReserveOrder.newBuilder().setReserveId("1").build()
        val tmpList = arrayListOf(order, order1)
        return tmpList
    }

    fun earlyPartSettled(money:BigDecimal){


    }

}