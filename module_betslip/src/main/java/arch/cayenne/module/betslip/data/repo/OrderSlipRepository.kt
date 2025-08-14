package arch.cayenne.module.betslip.data.repo

import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.database.dao.BetSlipOrderDao
import arch.cayenne.lib.database.dao.InfoDao
import arch.cayenne.lib.database.entity.BetSlipOrderBean
import arch.cayenne.module.betslip.BetSlipRemoteManager
import arch.cayenne.module.betslip.data.constants.CommonExtension.toOrderBean
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
            val data = result.data!!.orderList.map { it.toOrderBean(type, currency, null) }
            if (data.isEmpty()) {
                betSlipOrderDao.deleteByType(type)
            } else {
                betSlipOrderDao.insert(data)
                betSlipOrderDao.deleteMissing(type, data.map { it.betId })
            }
            ApiResponseState.Succeeded(data)
        } else {
            betSlipOrderDao.deleteByType(type)
            ApiResponseState.Failed(result.error)
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
            val data = result.data!!.orderList.map { it.toOrderBean(type, currency, matchId) }
            if (data.isEmpty()) {
                betSlipOrderDao.deleteByTypeAndMatchId(type, matchId)
            } else {
                betSlipOrderDao.insert(data)
                betSlipOrderDao.deleteMissingByMatchId(type, data.map { it.betId }, matchId)
            }
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
            val data = result.data!!.orderList.map { it.toOrderBean(type, currency, null) }
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
            val data = result.data!!.orderList.map { it.toOrderBean(type, currency, matchId) }
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