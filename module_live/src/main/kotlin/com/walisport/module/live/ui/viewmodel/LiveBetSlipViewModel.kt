package com.walisport.module.live.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import com.walisport.module.live.data.model.LiveBetSlipData
import com.walisport.module.live.data.repository.LiveBetRepository
import com.walisport.module.live.data.constants.LiveBetSlipEnum
import galaxy.common.proto.Common
import galaxy.common.proto.Common.ReserveOrder
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf


class LiveBetSlipViewModel : BaseViewModel() {
    private var matchId: Long = -1
    private var sportId: Int = -1
    private var page = 1
    private val pageSize = 10
    private val repository: LiveBetRepository by inject { parametersOf(viewModelScope) }
    private val _orderLiveData = MutableLiveData<List<Common.Order>?>()
    val orderLiveData: LiveData<List<Common.Order>?> = _orderLiveData
    private val _reserveLiveData = MutableLiveData<List<Common.ReserveOrder>?>()
    val reserveLiveData: LiveData<List<Common.ReserveOrder>?> = _reserveLiveData
    private val _earlySettledLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val earlySettledLiveData: LiveData<Boolean> = _earlySettledLiveData
    private val _cancelReserveLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val cancelReserveLiveData: LiveData<Boolean> = _cancelReserveLiveData
    private val _modifyOddsLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val modifyOddsLiveData: LiveData<Boolean> = _modifyOddsLiveData


    fun setIds(matchId: Long, sportId: Int) {
        this.matchId = matchId
        this.sportId = sportId
    }

    /**
     * 获取注单列表
     * */
    fun getOrders(status: LiveBetSlipEnum) {
        viewModelScope.launch {
            val result = repository.getOrderReq(status.value, page, pageSize, sportId, matchId)
            _orderLiveData.value = result
        }
    }

    /**
     * 获取注单预约单列表
     * */
    fun getReserveOrder() {
        viewModelScope.launch {
            val result = repository.getReserveOrder(sportId, matchId)
            _reserveLiveData.value = result
        }

    }


    fun getTestList(): List<LiveBetSlipData> {
        val order = Common.Order.newBuilder().setBetId("0").build()
        val order1 = Common.Order.newBuilder().setBetId("1").build()
        val tmpList = arrayListOf(LiveBetSlipData(order), LiveBetSlipData(order1))
        return tmpList
    }

    /**
     * 部分提前结算
     * */
    fun earlyPartSettled(order: Common.Order, money: String, expectPrice: String) {
        viewModelScope.launch {
            val result = repository.earlySettle(order.betId, money, expectPrice, false)
            _earlySettledLiveData.value = result?.success ?: false
        }
    }

    /**
     * 取消预约
     * */
    fun cancelReserve(order: Common.ReserveOrder) {
        viewModelScope.launch {
            val result = repository.reserveCancel(order.reserveId)
            _cancelReserveLiveData.value = result?.success ?: false
        }
    }

    /**
     * 修改预约
     * */
    fun modifyReserve(order: ReserveOrder, odds: String) {
        viewModelScope.launch {
            val result = repository.reserveUpdate(order.reserveId, order.betAmount, odds)
            _modifyOddsLiveData.value = result?.success ?: false
        }
    }

    fun refreshOrder(status: LiveBetSlipEnum) {
        page = 1
        getOrders(status)
    }

    fun loadMoreOrder(status: LiveBetSlipEnum) {
        viewModelScope.launch {
            page++
            val result = repository.getOrderReq(status.value, page, pageSize, sportId, matchId)
            _orderLiveData.value = result
            if (_orderLiveData.value?.isEmpty() == true) {
                page--
            }
            "loadMore $page".logd("remote")
        }
    }

}