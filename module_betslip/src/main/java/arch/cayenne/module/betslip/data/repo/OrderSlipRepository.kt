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
        return@withContext if (result.error == null && result.data != null) {
            val data = result.data!!.orderList.map { it.toOrderBean(type) }
            if (data.isEmpty()) {
                betSlipOrderDao.deleteByType(type)
            } else {
                val currency = infoDao.getCurrency()
                data.forEach { it.currency = currency }
                betSlipOrderDao.insert(data)
                betSlipOrderDao.deleteMissing(type, data.map { it.betId })
            }
            ApiResponseState.Succeeded(data)
        } else {
            betSlipOrderDao.deleteByType(type)
            ApiResponseState.Failed(result.error)
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
        if (result.error == null && result.data != null) {
            val data = result.data!!.orderList.map { it.toOrderBean(type) }
            val currency = infoDao.getCurrency()
            data.forEach { it.currency = currency }
            betSlipOrderDao.insert(data)
            ApiResponseState.Succeeded(data)
        } else {
            betSlipOrderDao.deleteByType(type)
            ApiResponseState.Failed(result.error)
        }
    }


    fun deleteAll(type: Int) {
        scope.launch {
            betSlipOrderDao.deleteByType(type)
        }
    }
}