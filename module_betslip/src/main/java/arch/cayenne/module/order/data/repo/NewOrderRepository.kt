package arch.cayenne.module.order.data.repo

import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.database.dao.BetSlipOrderDao
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewOrderRepository(
    scope: CoroutineScope,
    private val infoDao: InfoDao,
    remoteManager: BetSlipRemoteManager
) : BaseBetSlipRepository(scope, remoteManager) {

    private var notifyScope: Job? = null

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
//                if (this?.success == true) {
//                    betSlipOrderDao.updateToPendingEarlySettle(betId)
//                    registerEarlySettleNotify(matchId)
//                }
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
//            betSlipOrderDao.updateToPendingEarlySettle(betId, data.first().settleStatus)
//            val price = data.first().price.toFloatOrNull() ?: 0f
//            if (price <= 0f) {
//                betSlipOrderDao.updateCannotEarlySettle(betId)
//            }
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
//            if (newData.betAmount == newData.earlyBetAmount) {
//                betSlipOrderDao.updateBetSlipType(newData.betId, BetSlipEnum.Settled.value)
//            } else {
//                betSlipOrderDao.insert(newData)
//            }
        }
    }
}