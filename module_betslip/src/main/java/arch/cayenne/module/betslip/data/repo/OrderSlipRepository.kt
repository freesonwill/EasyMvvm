package arch.cayenne.module.betslip.data.repo

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
    ): List<BetSlipOrderBean>? {
        return withContext(scope.coroutineContext) {
            remoteManager.getOrderReq(
                type,
                startTime,
                endTime,
                cursorBetTime,
                size,
                if (sportIds.size == 1 && sportIds.first() == -1) null else sportIds,
                if (matchId == -1L) null else matchId)
        }.apply {
            if (this.isNullOrEmpty()) {
                betSlipOrderDao.deleteByType(type)
            } else {
                betSlipOrderDao.insert(this)
                betSlipOrderDao.deleteMissing(type, this.map { it.betId })
            }
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
    ): List<BetSlipOrderBean>? {
        return withContext(scope.coroutineContext) {
            remoteManager.getOrderReq(
                type,
                startTime,
                endTime,
                cursorBetTime,
                size,
                if (sportIds.size == 1 && sportIds.first() == -1) null else sportIds,
                if (matchId == -1L) null else matchId)
        }.apply {
            if (this == null) {
                betSlipOrderDao.deleteByType(type)
            } else {
                betSlipOrderDao.insert(this)
            }
        }
    }


    fun deleteAll(type: Int) {
        scope.launch {
            betSlipOrderDao.deleteByType(type)
        }
    }
}