package arch.cayenne.module.betslip.data.repo

import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.database.dao.BetSlipOrderDao
import arch.cayenne.lib.database.entity.BetSlipOrderBean
import arch.cayenne.module.betslip.BetSlipRemoteManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class OrderSlipRepository(
    scope: CoroutineScope,
    protected val betSlipOrderDao: BetSlipOrderDao,
    remoteManager: BetSlipRemoteManager
) : BaseBetSlipRepository(scope, remoteManager) {

    private val _observeOrderBeanFlow = MutableSharedFlow<List<BetSlipOrderBean>>()
    val observeOrderBeanFlow: Flow<List<BetSlipOrderBean>> = _observeOrderBeanFlow

    fun registerObserveOrderBean(type: Int) {
        scope.launch {
            betSlipOrderDao.observeOrderBean(type).collect {
                _observeOrderBeanFlow.emit(it)
            }
        }
    }

    suspend fun getOrder(
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
            if (sportIds.size == 1 && sportIds.first() == -1) null else sportIds,
            if (matchId == -1L) null else matchId
        )
        if (result.isNullOrEmpty()) {
            betSlipOrderDao.deleteByType(type)
        } else {
            betSlipOrderDao.insert(result)
            betSlipOrderDao.deleteMissing(type, result.map { it.betId })
        }
        return@withContext if (result == null) {
            ApiResponseState.Failed()
        } else {
            ApiResponseState.Succeeded(result)
        }
    }

    suspend fun loadMoreOrder(
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
            if (sportIds.size == 1 && sportIds.first() == -1) null else sportIds,
            if (matchId == -1L) null else matchId
        )
        return@withContext if (result == null) {
            betSlipOrderDao.deleteByType(type)
            ApiResponseState.Failed()
        } else {
            betSlipOrderDao.insert(result)
            ApiResponseState.Succeeded(result)
        }
    }


    fun deleteAll(type: Int) {
        scope.launch {
            betSlipOrderDao.deleteByType(type)
        }
    }
}