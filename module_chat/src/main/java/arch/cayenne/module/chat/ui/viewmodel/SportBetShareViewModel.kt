package arch.cayenne.module.chat.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.database.entity.BetSlipData
import arch.cayenne.lib.database.entity.BetSlipOrderBean
import arch.cayenne.module.betslip.data.repo.OrderSlipRepository
import arch.cayenne.module.order.data.constants.OrderSportPageEnum
import arch.cayenne.module.order.utils.TimeUtils

/**
 * @author: wenxi
 * @date: 8/12/25 20:28
 * @description:
 */
class SportBetShareViewModel(private val repo: OrderSlipRepository):BaseViewModel() {
    var isExpand = false


    private val _orderDataListener = MutableLiveData<List<BetSlipData>>()
    val orderDataListener: LiveData<List<BetSlipData>> = _orderDataListener

    fun setOrderData(type: OrderSportPageEnum, startTime: Long? = null, endTime: Long? = null) {
        callApi({
            repo.getOrder(type.value, startTime, endTime, null, 10)
        }, { resp ->
            if (resp is ApiResponseState.Succeeded<*>) {
                val data = resp.data as List<BetSlipOrderBean>
                setOrderData(data)
            }
        })
    }
    private fun setOrderData(data: List<BetSlipOrderBean>) {
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
                // 添加該日期的所有訂單（按時間排序，最新的在前）
                val sortedOrders = ordersForDate.sortedByDescending { it.betTime }
                newData.addAll(sortedOrders)
            }
        }
        if(newData.isNotEmpty()){
            _orderDataListener.postValue(arrayListOf(newData[0]))
        }
    }

    private fun groupOrdersByDate(orders: List<BetSlipOrderBean>): Map<String, List<BetSlipOrderBean>> {
        return orders.groupBy { order ->
            TimeUtils.formatTimeMillis(order.betTime)
        }
    }


}