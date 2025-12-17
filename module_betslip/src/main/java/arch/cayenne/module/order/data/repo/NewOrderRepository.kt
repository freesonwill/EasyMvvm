package arch.cayenne.module.order.data.repo

import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.database.dao.InfoDao
import arch.cayenne.lib.database.entity.BetSlipOrderBean
import arch.cayenne.module.betslip.BetSlipRemoteManager
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.data.constants.CommonExtension.toOrderBean
import arch.cayenne.module.betslip.data.repo.BaseBetSlipRepository
import galaxy.client.proto.Client
import galaxy.common.proto.Common
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewOrderRepository(
    scope: CoroutineScope,
    private val infoDao: InfoDao,
    remoteManager: BetSlipRemoteManager
) : BaseBetSlipRepository(scope, remoteManager) {

    private var notifyScope: Job? = null
    
    // 訂單資料的 Flow，供 ViewModel 監聽
    private val _orderDataFlow = MutableSharedFlow<List<BetSlipOrderBean>>()
    val orderDataFlow: Flow<List<BetSlipOrderBean>> = _orderDataFlow
    
    // 使用 MAP 來快速查找和管理訂單
    private val orderMap = mutableMapOf<String, BetSlipOrderBean>()

    /**
     * 獲取訂單資料
     */
    suspend fun getOrder(
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
        return@withContext if (result.error == null && result.data != null) {
            val currency = infoDao.getCurrency()
            val data = result.data!!.orderList.map {
                it.toOrderBean(type, currency, null)
            }
            // 更新內部資料
            updateOrderData(data)
            ApiResponseState.Succeeded(data)
        } else {
            ApiResponseState.Failed(result.error)
        }
    }

    /**
     * 提前結算
     */
    suspend fun earlySettle(
        matchId: Long,
        betId: String,
        amount: String,
        expectPrice: String,
        acceptPriceReduce: Boolean
    ): Client.EarlySettleResp? {
        return withContext(scope.coroutineContext) {
            remoteManager.earlySettleReq(betId, amount, expectPrice, acceptPriceReduce).apply {
                if (this?.success == true) {
                    // 更新本地訂單狀態為待結算
                    updateToPendingEarlySettle(betId, 102)
                    registerEarlySettleNotify(matchId)
                }
            }
        }
    }

    /**
     * 獲取提前結算價格
     */
    suspend fun earlySettledPrice(betId: String): List<Common.EarlySettlePrice>? = withContext(scope.coroutineContext) {
        val resp = remoteManager.earlySettlePriceReq(betId)
        val data = resp?.priceList
        if (!data.isNullOrEmpty()) {
            val settleStatus = data.first().settleStatus
            updateToPendingEarlySettle(betId, settleStatus)
            
            val price = data.first().price.toFloatOrNull() ?: 0f
            if (price <= 0f) {
                updateCannotEarlySettle(betId)
            }
        }
        resp?.priceList
    }

    private fun registerEarlySettleNotify(matchId: Long) {
        if (notifyScope == null) {
            notifyScope = scope.launch {
                remoteManager.registerEarlySettleNotify().collect { res ->
                    if (res.error == null && res.data != null) {
                        val currency = infoDao.getCurrency()
                        val newData = res.data!!.order.toOrderBean(BetSlipEnum.UnSettled.value, currency, matchId)
                        updateEarlySettleData(newData)
                    }
                }
            }
        }
    }

    private fun updateEarlySettleData(newData: BetSlipOrderBean) {
        scope.launch {
            if (newData.betAmount == newData.earlyBetAmount) {
                // 完全結算，更新訂單類型
                updateBetType(newData.betId, BetSlipEnum.Settled.value)
            } else {
                // 部分結算，添加新訂單
                addOrderData(newData)
            }
        }
    }

    /**
     * 更新或添加訂單資料（第一次載入或載入更多）
     */
    private fun updateOrderData(newOrders: List<BetSlipOrderBean>, isLoadMore: Boolean = false) {
        if (isLoadMore) {
            // 載入更多：添加到現有資料
            newOrders.forEach { order ->
                orderMap[order.betId] = order
            }
        } else {
            // 第一次載入或重新載入：替換所有資料
            orderMap.clear()
            newOrders.forEach { order ->
                orderMap[order.betId] = order
            }
        }
        
        // 發送更新的資料
        _orderDataFlow.tryEmit(orderMap.values.toList())
    }

    /**
     * 載入更多資料
     */
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
        return@withContext if (result.error == null && result.data != null) {
            val currency = infoDao.getCurrency()
            val data = result.data!!.orderList.map {
                it.toOrderBean(type, currency, null)
            }
            // 更新內部資料（載入更多模式）
            updateOrderData(data, isLoadMore = true)
            ApiResponseState.Succeeded(data)
        } else {
            ApiResponseState.Failed(result.error)
        }
    }

    /**
     * 根據 betId 更新訂單的 earlySettlePrice.settleStatus
     */
    private fun updateToPendingEarlySettle(betId: String, settleStatus: Int) {
        val order = orderMap[betId] ?: return
        val updatedEarlySettlePrice = order.earlySettlePrice.copy(settleStatus = settleStatus)
        val updatedOrder = order.copy(earlySettlePrice = updatedEarlySettlePrice)
        
        orderMap[betId] = updatedOrder
        _orderDataFlow.tryEmit(orderMap.values.toList())
    }

    /**
     * 根據 betId 更新訂單的 betSlipType
     */
    private fun updateBetType(betId: String, betSlipType: Int) {
        val order = orderMap[betId] ?: return
        val updatedOrder = order.copy(betSlipType = betSlipType)
        
        orderMap[betId] = updatedOrder
        _orderDataFlow.tryEmit(orderMap.values.toList())
    }

    /**
     * 添加新訂單資料
     */
    private fun addOrderData(newOrder: BetSlipOrderBean) {
        orderMap[newOrder.betId] = newOrder
        _orderDataFlow.tryEmit(orderMap.values.toList())
    }

    /**
     * 更新訂單為不支持提前結算
     */
    private fun updateCannotEarlySettle(betId: String) {
        val order = orderMap[betId] ?: return
        val updatedEarlySettlePrice = order.earlySettlePrice.copy(earlySupport = false)
        val updatedOrder = order.copy(earlySettlePrice = updatedEarlySettlePrice)
        
        orderMap[betId] = updatedOrder
        _orderDataFlow.tryEmit(orderMap.values.toList())
    }
}