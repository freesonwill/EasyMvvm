package arch.cayenne.module.betslip.data.repo

import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.common.utils.ext.ResourceExt
import arch.cayenne.lib.database.dao.BetSlipOrderDao
import arch.cayenne.lib.database.dao.InfoDao
import arch.cayenne.lib.database.entity.BetSlipOrderBean
import arch.cayenne.lib.websocket.data.SimpleResponseError
import arch.cayenne.module.betslip.BetSlipRemoteManager
import arch.cayenne.module.betslip.data.constants.CommonExtension.toOrderBean
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class OrderSlipRepository(
    scope: CoroutineScope,
    protected val betSlipOrderDao: BetSlipOrderDao,
    protected val infoDao: InfoDao,
    remoteManager: BetSlipRemoteManager
) : BaseBetSlipRepository(scope, remoteManager) {

    private val _observeOrderBeanFlow =
        MutableSharedFlow<List<BetSlipOrderBean>>(replay = 1, extraBufferCapacity = 1)
    val observeOrderBeanFlow: Flow<List<BetSlipOrderBean>> = _observeOrderBeanFlow

    fun registerObserveOrderBean(type: Int, liveMatchId: Long) {
        scope.launch {
            betSlipOrderDao.observeOrderBeanByMatchId(type, liveMatchId).collect {
                _observeOrderBeanFlow.emit(it)
            }
        }
    }

    suspend fun getOrder(
        type: Int,
        startTime: Long?,
        endTime: Long?,
        cursorBetTime: Long?,
        size: Int
    ): ApiResponseState = withContext(scope.coroutineContext) {
        val mockData = getTestMockData(startTime, endTime) ?: return@withContext ApiResponseState.Failed(
            SimpleResponseError()
        )
        return@withContext ApiResponseState.Succeeded(mockData)
//        val result = remoteManager.getOrderReq(
//            type,
//            startTime,
//            endTime,
//            cursorBetTime,
//            size,
//            null,
//            null
//        )
//        return@withContext if (result.error == null && result.data != null) {
//            val currency = infoDao.getCurrency()
//            val data = result.data!!.orderList.map {
//                it.toOrderBean(type, currency, null)
//            }
//
//            betSlipOrderDao.insert(data)
//            betSlipOrderDao.deleteMissing(type, data.map { it.betId })
//            ApiResponseState.Succeeded(data)
//        } else {
//            betSlipOrderDao.deleteByType(type)
//            ApiResponseState.Failed(result.error)
//        }
    }

    private fun getTestMockData(startTime: Long?, endTime: Long?): List<BetSlipOrderBean>? {
        val mockData = ResourceExt.getAssets("mock_order.json") ?: return null
        val originalData = Gson().fromJson(mockData, BetSlipOrderBean::class.java)
        
        val calendar = java.util.Calendar.getInstance()
        
        // 1. 原始資料（保持不變）
        val data1 = originalData
        
        // 2. 當前時間
        val currentTime = System.currentTimeMillis()
        val data2 = originalData.copy(betTime = currentTime)
        
        // 3. 昨天時間
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.add(java.util.Calendar.DAY_OF_MONTH, -1)
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 14)
        calendar.set(java.util.Calendar.MINUTE, 30)
        calendar.set(java.util.Calendar.SECOND, 0)
        val yesterdayTime = calendar.timeInMillis
        val data3 = originalData.copy(betTime = yesterdayTime)
        
        // 4. 上個月任意時間（上個月15號）
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.add(java.util.Calendar.MONTH, -1)
        calendar.set(java.util.Calendar.DAY_OF_MONTH, 15)
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 10)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        val lastMonthTime = calendar.timeInMillis
        val data4 = originalData.copy(betTime = lastMonthTime)
        
        // 5. 上週五時間
        calendar.timeInMillis = System.currentTimeMillis()
        val dayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK)
        // 計算到上週五的天數差
        val daysToLastFriday = when (dayOfWeek) {
            java.util.Calendar.SATURDAY -> 8  // 週六往前8天
            java.util.Calendar.SUNDAY -> 9    // 週日往前9天
            else -> dayOfWeek + 2             // 其他日子
        }
        calendar.add(java.util.Calendar.DAY_OF_MONTH, -daysToLastFriday)
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 18)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        val lastFridayTime = calendar.timeInMillis
        val data5 = originalData.copy(betTime = lastFridayTime)
        
        val allData = listOf(data1, data2, data3, data4, data5)
        
        // 若 startTime 和 endTime 都為 null，則不篩選
        if (startTime == null && endTime == null) {
            return allData
        }
        
        // 根據 startTime 和 endTime 過濾資料
        return allData.filter { bean ->
            val betTime = bean.betTime
            when {
                // 只有 startTime，篩選 startTime 之前的時間
                startTime != null && endTime == null -> betTime <= startTime
                // 只有 endTime，篩選 endTime 之後的時間
                startTime == null && endTime != null -> betTime >= endTime
                // 兩者都有，篩選在範圍內的時間
                startTime != null && endTime != null -> betTime >= startTime && betTime <= endTime
                else -> true
            }
        }
    }

    suspend fun getLiveOrder(
        type: Int,
        startTime: Long?,
        endTime: Long?,
        cursorBetTime: Long?,
        size: Int,
        sportIds: List<Int>,
        matchId: Long
    ): ApiResponseState = withContext(scope.coroutineContext) {
        val result = remoteManager.getOrderReq(
            type,
            startTime,
            endTime,
            cursorBetTime,
            size,
            sportIds,
            matchId
        )
        return@withContext if (result.error == null && result.data != null) {
            val currency = infoDao.getCurrency()
            val data = result.data!!.orderList.map {
                it.toOrderBean(type, currency, null)
            }

            betSlipOrderDao.insert(data)
            betSlipOrderDao.deleteMissingByMatchId(type, data.map { it.betId }, matchId)

            ApiResponseState.Succeeded(data)
        } else {
            betSlipOrderDao.deleteByTypeAndMatchId(type, matchId)
            ApiResponseState.Failed(result.error)
        }
    }

    suspend fun loadMoreOrder(
        type: Int,
        startTime: Long?,
        endTime: Long?,
        cursorBetTime: Long?,
        size: Int
    ): ApiResponseState = withContext(scope.coroutineContext) {
        val result = remoteManager.getOrderReq(
            type,
            startTime,
            endTime,
            cursorBetTime,
            size,
            null,
            null
        )
        if (result.error == null && result.data != null) {
            val currency = infoDao.getCurrency()
            val data = result.data!!.orderList.map {
                it.toOrderBean(type, currency, null)
            }
            betSlipOrderDao.insert(data)
            ApiResponseState.Succeeded(data)
        } else {
            betSlipOrderDao.deleteByType(type)
            ApiResponseState.Failed(result.error)
        }
    }

    suspend fun loadLiveMoreOrder(
        type: Int,
        startTime: Long?,
        endTime: Long?,
        cursorBetTime: Long?,
        size: Int,
        sportIds: List<Int>,
        matchId: Long,
    ): ApiResponseState = withContext(scope.coroutineContext) {
        val result = remoteManager.getOrderReq(
            type,
            startTime,
            endTime,
            cursorBetTime,
            size,
            sportIds,
            matchId
        )
        if (result.error == null && result.data != null) {
            val currency = infoDao.getCurrency()
            val data = result.data!!.orderList.map {
                it.toOrderBean(type, currency, null)
            }
            betSlipOrderDao.insert(data)
            ApiResponseState.Succeeded(data)
        } else {
            betSlipOrderDao.deleteByTypeAndMatchId(type, matchId)
            ApiResponseState.Failed(result.error)
        }
    }


    fun deleteAll(type: Int, matchId: Long) {
        scope.launch {
            betSlipOrderDao.deleteByTypeAndMatchId(type, matchId)
        }
    }
}