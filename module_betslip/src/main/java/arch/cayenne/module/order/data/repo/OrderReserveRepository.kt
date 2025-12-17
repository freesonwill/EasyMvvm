package arch.cayenne.module.order.data.repo

import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.common.utils.ext.ResourceExt
import arch.cayenne.lib.database.dao.InfoDao
import arch.cayenne.lib.database.entity.BetSlipReserveBean
import arch.cayenne.module.betslip.BetSlipRemoteManager
import arch.cayenne.module.betslip.data.constants.CommonExtension.toReserveOrderBean
import arch.cayenne.module.betslip.data.repo.BaseBetSlipRepository
import com.google.gson.Gson
import galaxy.client.proto.Client
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext

class OrderReserveRepository(
    scope: CoroutineScope,
    private val infoDao: InfoDao,
    remoteManager: BetSlipRemoteManager
) : BaseBetSlipRepository(scope, remoteManager) {

    // 預約資料的 Flow，供 ViewModel 監聽
    private val _reserveDataFlow = MutableSharedFlow<List<BetSlipReserveBean>>()
    val reserveDataFlow: Flow<List<BetSlipReserveBean>> = _reserveDataFlow

    // 使用 MAP 來快速查找和管理預約資料
    private val reserveMap = mutableMapOf<String, BetSlipReserveBean>()

    /**
     * 獲取預約資料
     */
    suspend fun getReserveOrder(
        startTime: Long?,
        endTime: Long?,
        cursorBetTime: Long?,
        size: Int,
    ): ApiResponseState = withContext(scope.coroutineContext) {
        val result = remoteManager.getReserveOrder(
            startTime,
            endTime,
            null,
            null,
            cursorBetTime,
            size
        )
        return@withContext if (result.error == null && result.data != null) {
            val currency = infoDao.getCurrency()
            // 這裡需要將 API 返回的資料轉換為 BetSlipReserveBean
            // 假設 API 返回的是預約資料格式
            val data = result.data!!.orderList.map {
                // 需要實現 toReserveBean 轉換方法
                it.toReserveOrderBean(currency, null)
            }
            // 更新內部資料
            updateReserveData(data)
            ApiResponseState.Succeeded(data)
        } else {
            ApiResponseState.Failed(result.error)
        }
    }

    /**
     * 載入更多預約資料
     */
    suspend fun loadMoreReserveOrder(
        startTime: Long?,
        endTime: Long?,
        cursorBetTime: Long?,
        size: Int,
    ): ApiResponseState = withContext(scope.coroutineContext) {
        val result = remoteManager.getReserveOrder(
            startTime,
            endTime,
            null,
            null,
            cursorBetTime,
            size
        )
        return@withContext if (result.error == null && result.data != null) {
            val currency = infoDao.getCurrency()
            val data = result.data!!.orderList.map {
                it.toReserveOrderBean(currency, null)
            }
            // 更新內部資料（載入更多模式）
            updateReserveData(data, isLoadMore = true)
            ApiResponseState.Succeeded(data)
        } else {
            ApiResponseState.Failed(result.error)
        }
    }

    /**
     * 取消預約
     */
    suspend fun cancelReserve(reserveId: String): Client.ReserveCancelResp? {
        return remoteManager.reserveCancelReq(reserveId).apply {
            if (this?.success == true) {
                deleteReserveFromMap(reserveId)
            }
        }
    }

    /**
     * 修改預約賠率
     */
    suspend fun modifyReserveOdds(
        reserveId: String,
        amount: Long,
        newOdds: Int
    ): Client.ReserveUpdateResp? {
        return remoteManager.reserveUpdateReq(reserveId, amount, newOdds).apply {
            if (this?.success == true) {
                updateReserveOdds(reserveId, newOdds)
            }
        }
    }

    /**
     * 更新或添加預約資料
     */
    private fun updateReserveData(
        newReserves: List<BetSlipReserveBean>,
        isLoadMore: Boolean = false
    ) {
        if (isLoadMore) {
            // 載入更多：添加到現有資料
            newReserves.forEach { reserve ->
                reserveMap[reserve.reserveId] = reserve
            }
        } else {
            // 第一次載入或重新載入：替換所有資料
            reserveMap.clear()
            newReserves.forEach { reserve ->
                reserveMap[reserve.reserveId] = reserve
            }
        }

        // 發送更新的資料
        _reserveDataFlow.tryEmit(reserveMap.values.toList())
    }

    /**
     * 根據 reserveId 更新預約賠率
     */
    private fun updateReserveOdds(reserveId: String, newOdds: Int) {
        val reserve = reserveMap[reserveId] ?: return
        val updatedSelection = reserve.selection.copy(odds = newOdds)
        val updatedReserve = reserve.copy(selection = updatedSelection)

        reserveMap[reserveId] = updatedReserve
        _reserveDataFlow.tryEmit(reserveMap.values.toList())
    }

    /**
     * 從 map 中刪除指定的預約資料
     */
    private fun deleteReserveFromMap(reserveId: String) {
        reserveMap.remove(reserveId)
        _reserveDataFlow.tryEmit(reserveMap.values.toList())
    }

    fun loadMockTestData() {
        val mockData = ResourceExt.getAssets("mock_order_reserve.json") ?: return
        val originalData = Gson().fromJson(mockData, BetSlipReserveBean::class.java)

        // 將讀取到的資料寫入 MAP
        reserveMap[originalData.reserveId] = originalData
        
        // emit 到 Flow 中
        _reserveDataFlow.tryEmit(reserveMap.values.toList())
    }
}