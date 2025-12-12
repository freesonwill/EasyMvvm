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
import arch.cayenne.module.order.data.repo.NewOrderRepository
import arch.cayenne.module.order.data.constants.OrderSportPageEnum
import arch.cayenne.module.order.utils.TimeUtils
import galaxy.common.proto.Common
import kotlinx.coroutines.launch

class OrderSportPageViewModel(private val repo: NewOrderRepository) : BaseViewModel() {

    companion object {
        private const val PAGE_SIZE = 10
    }

    private val _intentEvent = MutableLiveData<Event<DataState>>()
    val intentEvent: LiveData<Event<DataState>> get() = _intentEvent

    private val _earlySettledResultLiveData: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val earlySettledResultLiveData: LiveData<Event<Boolean>> = _earlySettledResultLiveData

    private val _isSupportEarlySettleLiveData = MutableLiveData<Common.EarlySettlePrice>()
    val isSupportEarlySettleLiveData: LiveData<Common.EarlySettlePrice> = _isSupportEarlySettleLiveData

    var selectOrder: BetSlipOrderBean? = null
        private set

    private val _orderDataListener = MutableLiveData<List<BetSlipData>>()
    val orderDataListener: LiveData<List<BetSlipData>> = _orderDataListener

    private var lastCursorBetTime: Long? = null
    private var startTime: Long? = null
    private var endTime: Long? = null
    private var currentType: OrderSportPageEnum? = null
    private var canLoadMore = true

    /**
     * 第一筆資料載入
     * @param type 訂單類型
     * @param startTime 開始時間
     * @param endTime 結束時間
     */
    fun setOrderData(type: OrderSportPageEnum, startTime: Long? = null, endTime: Long? = null) {
        if (checkNetwork()) {
            this.currentType = type
            this.startTime = startTime
            this.endTime = endTime
            this.lastCursorBetTime = null // 第一筆載入不需要 cursor
            this.canLoadMore = true
            
            callApi({
                repo.getOrder(type.value, startTime, endTime, null, PAGE_SIZE)
            }, { resp ->
                handleOrderResponse(resp, isLoadMore = false)
                if (resp is ApiResponseState.Succeeded<*>) {
                    val data = resp.data as  List<BetSlipOrderBean>
                    _intentEvent.value = if (startTime != null && endTime != null && endTime != null && data.isEmpty()) {
                        Event(DataState.DataEmpty)
                    } else {
                        Event(DataState.None)
                    }
                }
            })
        }
    }

    /**
     * 載入更多資料
     */
    fun loadMore() {
        if (checkNetwork() && canLoadMore && currentType != null && lastCursorBetTime != null) {
            callApi({
                repo.getOrder(currentType!!.value, startTime, endTime, lastCursorBetTime, PAGE_SIZE)
            }, { resp ->
                handleOrderResponse(resp, isLoadMore = true)
            })
        }
    }

    /**
     * 重新載入資料
     */
    fun refresh() {
        if (checkNetwork() && currentType != null) {
            this.lastCursorBetTime = null // 重新載入不需要 cursor
            this.canLoadMore = true
            
            callApi({
                repo.getOrder(currentType!!.value, startTime, endTime, null, PAGE_SIZE)
            }, { resp ->
                handleOrderResponse(resp, isLoadMore = false)
            })
        }
    }

    /**
     * 檢查是否可以載入更多
     */
    fun canLoadMore(): Boolean = canLoadMore

    /**
     * 統一處理訂單 API 響應
     * @param resp API 響應
     * @param isLoadMore 是否為載入更多操作
     */
    private fun handleOrderResponse(resp: ApiResponseState, isLoadMore: Boolean) {
        if (resp is ApiResponseState.Succeeded<*>) {
            val newData = resp.data as List<BetSlipOrderBean>
            
            if (newData.isNotEmpty()) {
                val finalData = if (isLoadMore) {
                    // 載入更多：合併現有資料與新資料
                    val currentOrders = getCurrentOrderBeans()
                    currentOrders + newData
                } else {
                    // 第一次載入或重新載入：直接使用新資料
                    newData
                }
                
                processOrderData(finalData)
                lastCursorBetTime = newData.last().betTime
                canLoadMore = newData.size >= PAGE_SIZE // 如果返回資料 < 10，表示沒有更多資料
            } else {
                canLoadMore = false
            }
        }
    }

    /**
     * 從當前 LiveData 中提取所有的 OrderBean 資料
     */
    private fun getCurrentOrderBeans(): List<BetSlipOrderBean> {
        return _orderDataListener.value?.filterIsInstance<BetSlipOrderBean>() ?: emptyList()
    }

    private fun processOrderData(data: List<BetSlipOrderBean>) {
        // 按日期分組訂單
        val dataMap = groupOrdersByDate(data)

        // 按日期排序（越接近現在的越前面）
        val sortedDateKeys = dataMap.keys.sortedByDescending { dateString ->
            // 找到該日期組中最新的訂單時間來排序
            dataMap[dateString]?.maxOfOrNull { it.betTime } ?: 0L
        }
        val newData = mutableListOf<BetSlipData>()

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
            _intentEvent.value = Event(DataState.NetworkUnavailable)
            return false
        }
        return true
    }
}