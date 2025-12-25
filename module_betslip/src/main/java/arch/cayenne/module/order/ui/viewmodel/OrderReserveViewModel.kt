package arch.cayenne.module.order.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.common.ui.viewmodel.Event
import arch.cayenne.lib.database.entity.BetSlipData
import arch.cayenne.lib.database.entity.BetSlipOrderHeaderBean
import arch.cayenne.lib.database.entity.BetSlipReserveBean
import arch.cayenne.module.order.data.repo.OrderReserveRepository
import arch.cayenne.module.order.utils.TimeUtils
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class OrderReserveViewModel(private val repo: OrderReserveRepository) : BaseViewModel() {

    companion object {
        private const val PAGE_SIZE = 10
    }

    private val _intentEvent = MutableLiveData<Event<DataState>>()
    val intentEvent: LiveData<Event<DataState>> get() = _intentEvent

    private val _cancelReserveResultLiveData: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val cancelReserveResultLiveData: LiveData<Event<Boolean>> = _cancelReserveResultLiveData

    private val _modifyReserveResultLiveData: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val modifyReserveResultLiveData: LiveData<Event<Boolean>> = _modifyReserveResultLiveData

    private val _orderDataListener = MutableLiveData<List<BetSlipData>>()
    val orderDataListener: LiveData<List<BetSlipData>> = _orderDataListener

    init {
        // 監聽 Repository 的預約資料 Flow
        repo.reserveDataFlow
            .onEach { reserves ->
                Log.d("abcd", "+++ $reserves")

                if (reserves.isNotEmpty()) {
                    processReserveData(reserves)
                }
            }
            .launchIn(viewModelScope)
        repo.loadMockTestData()
    }

    private var lastCursorBetTime: Long? = null
    private var startTime: Long? = null
    private var endTime: Long? = null
    private var canLoadMore = true

    /**
     * 第一筆資料載入
     * @param startTime 開始時間
     * @param endTime 結束時間
     */
    fun setReserveData(startTime: Long? = null, endTime: Long? = null) {
        if (checkNetwork()) {
            this.startTime = startTime
            this.endTime = endTime
            this.lastCursorBetTime = null // 第一筆載入不需要 cursor
            this.canLoadMore = true
            
            callApi({
                repo.getReserveOrder(startTime, endTime, null, PAGE_SIZE)
            }, { resp ->
                if (resp is ApiResponseState.Succeeded<*>) {
                    val data = resp.data as List<BetSlipReserveBean>
                    _intentEvent.value = if (startTime != null && endTime != null && data.isEmpty()) {
                        Event(DataState.DataEmpty)
                    } else {
                        Event(DataState.None)
                    }
                    
                    if (data.isNotEmpty()) {
                        lastCursorBetTime = data.last().reserveTime
                        canLoadMore = data.size >= PAGE_SIZE
                    } else {
                        canLoadMore = false
                    }
                }
            })
        }
    }

    /**
     * 載入更多資料
     */
    fun loadMore() {
        if (checkNetwork() && canLoadMore && lastCursorBetTime != null) {
            callApi({
                repo.loadMoreReserveOrder(startTime, endTime, lastCursorBetTime, PAGE_SIZE)
            }, { resp ->
                if (resp is ApiResponseState.Succeeded<*>) {
                    val data = resp.data as List<BetSlipReserveBean>
                    if (data.isNotEmpty()) {
                        lastCursorBetTime = data.last().reserveTime
                        canLoadMore = data.size >= PAGE_SIZE
                    } else {
                        canLoadMore = false
                    }
                }
            })
        }
    }

    /**
     * 重新載入資料
     */
    fun refresh() {
        if (checkNetwork()) {
            this.lastCursorBetTime = null // 重新載入不需要 cursor
            this.canLoadMore = true
            
            callApi({
                repo.getReserveOrder(startTime, endTime, null, PAGE_SIZE)
            }, { resp ->
                if (resp is ApiResponseState.Succeeded<*>) {
                    val data = resp.data as List<BetSlipReserveBean>
                    if (data.isNotEmpty()) {
                        lastCursorBetTime = data.last().reserveTime
                        canLoadMore = data.size >= PAGE_SIZE
                    } else {
                        canLoadMore = false
                    }
                }
            })
        }
    }

    /**
     * 檢查是否可以載入更多
     */
    fun canLoadMore(): Boolean = canLoadMore

    /**
     * 取消預約
     */
    fun cancelReserve(order: BetSlipReserveBean) {
        viewModelScope.launch {
            val result = repo.cancelReserve(order.reserveId)
            _cancelReserveResultLiveData.value = Event(result?.success ?: false)
        }
    }

    /**
     * 修改預約賠率
     */
    fun modifyReserveOdds(order: BetSlipReserveBean, newOdds: Int) {
        viewModelScope.launch {
            val result = repo.modifyReserveOdds(order.reserveId, order.betAmount, newOdds)
            _modifyReserveResultLiveData.value = Event(result?.success ?: false)
        }
    }

    /**
     * 處理從 Repository 收到的預約資料，添加 Header 並更新 LiveData
     */
    private fun processReserveData(reserves: List<BetSlipReserveBean>) {
        // 如果預約列表為空，直接設置空列表
        if (reserves.isEmpty()) {
            _orderDataListener.postValue(emptyList())
            return
        }

        val processedData = buildReserveDataWithHeaders(reserves)
        _orderDataListener.postValue(processedData)
    }

    /**
     * 統一的資料處理方法：按日期分組並創建 header
     */
    private fun buildReserveDataWithHeaders(reserves: List<BetSlipReserveBean>): List<BetSlipData> {
        // 按日期分組預約
        val dataMap = reserves.groupBy { reserve ->
            TimeUtils.formatTimeMillis(reserve.reserveTime)
        }

        // 按日期排序（越接近現在的越前面）
        val sortedDateKeys = dataMap.keys.sortedByDescending { dateString ->
            // 找到該日期組中最新的預約時間來排序
            dataMap[dateString]?.maxOfOrNull { it.reserveTime } ?: 0L
        }
        
        val newData = mutableListOf<BetSlipData>()

        // 為每個日期組創建 header 和添加預約
        sortedDateKeys.forEach { dateString ->
            val reservesForDate = dataMap[dateString] ?: emptyList()

            if (reservesForDate.isNotEmpty()) {
                // 計算該日期的投注總額
                val dayBetAmount = reservesForDate.sumOf { it.betAmount }
                val dayValidBetAmount = reservesForDate.sumOf { it.betAmount }

                // 創建該日期的 header
                val header = BetSlipOrderHeaderBean(
                    dateTime = dateString,
                    currency = reservesForDate.firstOrNull()?.currency ?: "CNY",
                    betAmount = dayBetAmount,
                    validBetAmount = dayValidBetAmount
                )

                // 添加 header
                newData.add(header)

                // 添加該日期的所有預約（按時間排序，最新的在前）
                val sortedReserves = reservesForDate.sortedByDescending { it.reserveTime }
                newData.addAll(sortedReserves)
            }
        }
        
        return newData
    }

    fun checkNetwork(): Boolean {
        if (!repo.isConnected) {
            _intentEvent.value = Event(DataState.NetworkUnavailable)
            return false
        }
        return true
    }
}