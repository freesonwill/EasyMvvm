package arch.cayenne.module.betslip.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import arch.cayenne.lib.common.ui.viewmodel.Event
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.data.model.BetSlipData
import arch.cayenne.module.betslip.data.repo.BetSlipRepository
import arch.cayenne.module.betslip.utisl.BetSlipUtils.toBetSlipData
import galaxy.common.proto.Common.EarlySettlePrice
import galaxy.common.proto.Common.Order
import galaxy.common.proto.Common.ReserveOrder
import kotlinx.coroutines.launch


class BetSlipViewModel(private val repository: BetSlipRepository) : BaseViewModel() {

    companion object {
        private const val SIZE = 10
    }

    private var ids: Pair<Long, Int> = Pair(-1, -1)
    private val matchId: Long get() = ids.first
    private val sportId: Int get() = ids.second

    private var times: Pair<Long?, Long?> = Pair(null, null)
    private val startTime: Long? get() = times.first
    private val endTime: Long? get() = times.second

    //普通注单
    private val _orderLiveData = MutableLiveData<List<BetSlipData>>()
    val orderLiveData: LiveData<List<BetSlipData>> = _orderLiveData

    //预约注单
    private val _reserveLiveData = MutableLiveData<List<BetSlipData>>()
    val reserveLiveData: LiveData<List<BetSlipData>> = _reserveLiveData

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
    private val _isSupportEarlySettleLiveData = MutableLiveData<EarlySettlePrice>()
    val isSupportEarlySettleLiveData: LiveData<EarlySettlePrice> = _isSupportEarlySettleLiveData


    private val _state = MutableLiveData<Event<DynamicStateLayout.States>>()
    val state: LiveData<Event<DynamicStateLayout.States>> = _state

    //选择的提前结算注单
    var selectOrder: Order? = null
        private set

    fun setIds(matchId: Long, sportId: Int) {
        this.ids = Pair(matchId, sportId)
    }

    /**
     * 获取注单列表
     * */
    fun getOrders(status: BetSlipEnum) {
        viewModelScope.launch {
            repository.getOrderReq(
                status.value,
                startTime,
                endTime,
                0L,
                SIZE,
                sportId,
                matchId,
            )?.let { result ->
                _state.value = Event(if(result.isEmpty()) DynamicStateLayout.States.DATA_EMPTY else DynamicStateLayout.States.NULL)
                _orderLiveData.value = result.toBetSlipData()
            } ?: run {
                _state.value = Event(DynamicStateLayout.States.NETWORK_ANOMALY)
            }
        }
    }

    /**
     * 获取注单预约单列表
     * */
    fun getReserveOrder() {
        viewModelScope.launch {
            repository.getReserveOrder(
                startTime,
                endTime,
                sportId,
                matchId,
                0L,
                SIZE
            )?.let { result ->
                _state.value = Event(if(result.isEmpty()) DynamicStateLayout.States.DATA_EMPTY else DynamicStateLayout.States.NULL)
                _reserveLiveData.value = result.map { BetSlipData(reserve = it) }.toList()
            } ?: run {
                _state.value = Event(DynamicStateLayout.States.NETWORK_ANOMALY)
            }
        }
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

    fun refreshOrder(status: BetSlipEnum) {
        getOrders(status)
    }

    fun loadMoreOrder(status: BetSlipEnum) {
        val list = _orderLiveData.value
        viewModelScope.launch {
            repository.getOrderReq(
                status.value,
                startTime,
                endTime,
                0L,
                SIZE,
                sportId,
                matchId,
            )?.let { result ->
                _state.value = Event(DynamicStateLayout.States.NULL)
                if (result.isNotEmpty()) {
                    val newList = mutableListOf<BetSlipData>()
                    val oldList = _orderLiveData.value ?: emptyList()
                    val resultList = result.toBetSlipData()
                    newList.addAll(oldList)
                    newList.addAll(resultList)
                    _orderLiveData.value = newList
                }
            } ?: run {
                _state.value = Event(DynamicStateLayout.States.NETWORK_ANOMALY)
            }
        }
    }

    fun loadMoreReserve() {
        val list = _reserveLiveData.value
        viewModelScope.launch {
            repository.getReserveOrder(
                startTime,
                endTime,
                sportId,
                matchId,
                0L,
                SIZE
            )?.let { result ->
                _state.value = Event(DynamicStateLayout.States.NULL)
                if (result.isNotEmpty()) {
                    val newList = mutableListOf<BetSlipData>()
                    val oldList = _reserveLiveData.value ?: emptyList()
                    val resultList = result.map { BetSlipData(reserve = it) }
                    newList.addAll(oldList)
                    newList.addAll(resultList)
                    _reserveLiveData.value = newList
                }
            } ?: run {
                _state.value = Event(DynamicStateLayout.States.NETWORK_ANOMALY)
            }
        }
    }

    /**
     * 检查是否支持提前结算
     * */
    fun isSuppportEarlySettled(order: Order) {
        selectOrder = order
        viewModelScope.launch {
            val result = repository.earlySettledPrice(order.betId)
            if (!result.isNullOrEmpty()) {
                _isSupportEarlySettleLiveData.value = result.first()
            }
        }
    }

    fun setTime(startTime: Long?, endTime: Long?) {
        this.times = Pair(startTime, endTime)
    }

    fun loadData(status: BetSlipEnum) {
        if (status == BetSlipEnum.Reserve) {
            getReserveOrder()
        } else {
            getOrders(status)
        }
    }

    /**
     * 预约注单如果列表为空或者不是整页数据，则不加载更多
     *
     * */
    fun isReserveLoadMore() = !(_reserveLiveData.value.isNullOrEmpty() || (_reserveLiveData.value!!.size % SIZE != 0))

    /**
     * 注单如果列表为空或者不是整页数据，则不加载更多
     * */
    fun isOrderLoadMore() = !(_orderLiveData.value.isNullOrEmpty() || (_orderLiveData.value!!.size % SIZE != 0))

}