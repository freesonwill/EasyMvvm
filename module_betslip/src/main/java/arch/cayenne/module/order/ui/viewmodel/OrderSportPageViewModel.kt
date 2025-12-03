package arch.cayenne.module.order.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.common.ui.viewmodel.Event
import arch.cayenne.lib.database.entity.BetSlipData
import arch.cayenne.lib.database.entity.BetSlipOrderBean
import arch.cayenne.lib.database.entity.BetSlipOrderHeaderBean
import arch.cayenne.module.betslip.data.repo.UnsettleRepository
import arch.cayenne.module.order.data.constants.OrderSportPageEnum
import arch.cayenne.module.order.utils.TimeUtils
import galaxy.common.proto.Common
import kotlinx.coroutines.launch

class OrderSportPageViewModel(private val repo: UnsettleRepository) : BaseViewModel() {

    private val _networkConnectedEvent = MutableLiveData<Event<DataState>>()
    val networkConnectedEvent: LiveData<Event<DataState>> get() = _networkConnectedEvent

    private val _earlySettledResultLiveData: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val earlySettledResultLiveData: LiveData<Event<Boolean>> = _earlySettledResultLiveData

    private val _isSupportEarlySettleLiveData = MutableLiveData<Common.EarlySettlePrice>()
    val isSupportEarlySettleLiveData: LiveData<Common.EarlySettlePrice> = _isSupportEarlySettleLiveData

    var selectOrder: BetSlipOrderBean? = null
        private set

    private val _orderDataListener = MutableLiveData<List<BetSlipData>>()
    val orderDataListener: LiveData<List<BetSlipData>> = _orderDataListener

    fun setType(type: OrderSportPageEnum) {
        callApi({
            repo.getOrder(type.value, null, null, null, 10)
        }, { resp ->
            if (resp is ApiResponseState.Succeeded<*>) {
                val newData = mutableListOf<BetSlipData>()
                val data = resp.data as List<BetSlipOrderBean>

                // 按日期分組訂單
                val dataMap = groupOrdersByDate(data)

                // 按日期排序（越接近現在的越前面）
                val sortedDateKeys = dataMap.keys.sortedByDescending { dateString ->
                    // 找到該日期組中最新的訂單時間來排序
                    dataMap[dateString]?.maxOfOrNull { it.betTime } ?: 0L
                }

                // 為每個日期組創建 header 和添加訂單
                sortedDateKeys.forEach { dateString ->
                    val ordersForDate = dataMap[dateString] ?: emptyList()

                    if (ordersForDate.isNotEmpty()) {
                        // 計算該日期的投注總額
                        val dayBetAmount = ordersForDate.sumOf { it.betAmount }
                        val dayValidBetAmount = ordersForDate.sumOf { it.betAmount }

                        // 創建該日期的 header
                        val header = BetSlipOrderHeaderBean(
                            dateTime = dateString,
                            currency = ordersForDate.firstOrNull()?.currency ?: "CNY",
                            betAmount = dayBetAmount,
                            validBetAmount = dayValidBetAmount
                        )

                        // 添加 header
                        newData.add(header)

                        // 添加該日期的所有訂單（按時間排序，最新的在前）
                        val sortedOrders = ordersForDate.sortedByDescending { it.betTime }
                        newData.addAll(sortedOrders)
                    }
                }

                _orderDataListener.postValue(newData)
            }
        })
    }

    private fun groupOrdersByDate(orders: List<BetSlipOrderBean>): Map<String, List<BetSlipOrderBean>> {
        return orders.groupBy { order ->
            TimeUtils.formatTimeMillis(order.betTime)
        }
    }

    /**
     * 提前结算
     * */
    fun earlyPartSettled(betId: String, money: String, expectPrice: String) {
        viewModelScope.launch {
            val result = repo.earlySettle(-1, betId, money, expectPrice, false)
            _earlySettledResultLiveData.value = Event(result?.success ?: false)
        }
    }

    /**
     * 检查是否支持提前结算
     * */
    fun isSupportEarlySettled(order: BetSlipOrderBean) {
        if (checkNetwork()) {
            selectOrder = order
            viewModelScope.launch {
                val result = repo.earlySettledPrice(order.betId)
                if (!result.isNullOrEmpty()) {
                    _isSupportEarlySettleLiveData.value = result.first()
                }
            }
        }
    }

    private fun checkNetwork(): Boolean {
        if (!repo.isConnected) {
            _networkConnectedEvent.value = Event(DataState.NetworkUnavailable)
            return false
        }
        return true
    }
}