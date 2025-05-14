package arch.cayenne.module.betslip.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import galaxy.common.proto.Common.EarlySettlePrice
import galaxy.common.proto.Common.Order
import galaxy.common.proto.Common.ReserveOrder
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf


class LiveBetSlipViewModel : BaseViewModel() {
    private var matchId: Long = -1
    private var sportId: Int = -1
    private var page = 1
    private val pageSize = 10
    private val repository: arch.cayenne.module.betslip.data.repo.LiveBetRepository by inject { parametersOf(viewModelScope) }
    //普通注单
    private val _orderLiveData = MutableLiveData<List<Order>?>()
    val orderLiveData: LiveData<List<Order>?> = _orderLiveData
    //预约注单
    private val _reserveLiveData = MutableLiveData<List<ReserveOrder>?>()
    val reserveLiveData: LiveData<List<ReserveOrder>?> = _reserveLiveData
    //提前结算结果
    private val _earlySettledResultLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val earlySettledResultLiveData: LiveData<Boolean> = _earlySettledResultLiveData
    //取消预约
    private val _cancelReserveLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val cancelReserveLiveData: LiveData<Boolean> = _cancelReserveLiveData
    //修改赔率
    private val _modifyOddsLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val modifyOddsLiveData: LiveData<Boolean> = _modifyOddsLiveData
    //检查提前结算
    private val _earlySettlePriceLiveData = MutableLiveData<EarlySettlePrice>()
    val earlySettlePriceLiveData:LiveData<EarlySettlePrice> = _earlySettlePriceLiveData
    //选择的提前结算注单
    var selectOrder:Order? = null

    fun setIds(matchId: Long, sportId: Int) {
        this.matchId = matchId
        this.sportId = sportId
    }

    /**
     * 获取注单列表
     * */
    fun getOrders(status: arch.cayenne.module.betslip.data.constants.LiveBetSlipEnum) {
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


    fun getTestList(): List<arch.cayenne.module.betslip.data.model.LiveBetSlipData> {
        val order = Order.newBuilder().setBetId("0").build()
        val order1 = Order.newBuilder().setBetId("1").build()
        val tmpList = arrayListOf(
            arch.cayenne.module.betslip.data.model.LiveBetSlipData(order),
            arch.cayenne.module.betslip.data.model.LiveBetSlipData(order1)
        )
        return tmpList
    }

    /**
     * 部分提前结算
     * */
    fun earlyPartSettled(betId: String, money: String, expectPrice: String) {
        viewModelScope.launch {
            val result = repository.earlySettle(betId, money, expectPrice, false)
            _earlySettledResultLiveData.value = result?.success ?: false
        }
    }

    /**
     * 取消预约
     * */
    fun cancelReserve(order: ReserveOrder) {
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

    fun refreshOrder(status: arch.cayenne.module.betslip.data.constants.LiveBetSlipEnum) {
        page = 1
        getOrders(status)
    }

    fun loadMoreOrder(status: arch.cayenne.module.betslip.data.constants.LiveBetSlipEnum) {
        viewModelScope.launch {
            page++
            val result = repository.getOrderReq(status.value, page, pageSize, sportId, matchId)
            _orderLiveData.value = result
            if (_orderLiveData.value?.isEmpty() == true) {
                page--
            }
        }
    }

    /**
     * 检查是否支持提前结算
     * */
    fun earlySettledPrice(betId:String) {
        viewModelScope.launch {
            val result = repository.earlySettledPrice(betId)
            if (!result.isNullOrEmpty()) {
                _earlySettlePriceLiveData.value = result.first()
            }
        }
    }

}