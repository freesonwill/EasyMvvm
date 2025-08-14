package arch.cayenne.module.betslip.data.repo

import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.database.dao.BetSlipReserveDao
import arch.cayenne.lib.database.dao.InfoDao
import arch.cayenne.lib.database.entity.BetSlipReserveBean
import arch.cayenne.module.betslip.BetSlipRemoteManager
import arch.cayenne.module.betslip.data.constants.CommonExtension.toReserveOrderBean
import galaxy.client.proto.Client
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReserveSlipRepository(
    scope: CoroutineScope,
    private val betSlipReserveDao: BetSlipReserveDao,
    private val infoDao: InfoDao,
    remoteManager: BetSlipRemoteManager
) : BaseBetSlipRepository(scope, remoteManager) {

    private val _observeReserveBeanFlow =
        MutableSharedFlow<List<BetSlipReserveBean>>(replay = 1, extraBufferCapacity = 1)
    val observeReserveBeanFlow: Flow<List<BetSlipReserveBean>> = betSlipReserveDao.observeReserveBean()

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
            val data = result.data!!.orderList.map { it.toReserveOrderBean(currency, null) }
            if (data.isEmpty()) {
                betSlipReserveDao.deleteAll()
            } else {
                betSlipReserveDao.insert(data)
                betSlipReserveDao.deleteMissing(data.map { it.reserveId })
            }
            ApiResponseState.Succeeded(data)
        } else {
            betSlipReserveDao.deleteAll()
            ApiResponseState.Failed(result.error)
        }
    }

    suspend fun getLiveReserveOrder(
        startTime: Long?,
        endTime: Long?,
        sportIds: List<Int>,
        matchId: Long,
        cursorBetTime: Long?,
        size: Int,
    ): ApiResponseState = withContext(scope.coroutineContext) {
        val result = remoteManager.getReserveOrder(
            startTime,
            endTime,
            sportIds,
            matchId,
            cursorBetTime,
            size
        )
        return@withContext if (result.error == null && result.data != null) {
            val currency = infoDao.getCurrency()
            val data = result.data!!.orderList.map { it.toReserveOrderBean(currency, matchId) }
            if (data.isEmpty()) {
                betSlipReserveDao.deleteAllByMatchId(matchId)
            } else {
                betSlipReserveDao.insert(data)
                betSlipReserveDao.deleteMissingByMatchId(matchId, data.map { it.reserveId })
            }
            ApiResponseState.Succeeded(data)
        } else {
            betSlipReserveDao.deleteAllByMatchId(matchId)
            ApiResponseState.Failed(result.error)
        }
    }

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
            val data = result.data!!.orderList.map { it.toReserveOrderBean(currency, null) }
            betSlipReserveDao.insert(data)
            ApiResponseState.Succeeded(data)
        } else {
            betSlipReserveDao.deleteAll()
            ApiResponseState.Failed(result.error)
        }
    }

    suspend fun loadLiveMoreReserveOrder(
        startTime: Long?,
        endTime: Long?,
        sportIds: List<Int>,
        matchId: Long,
        cursorBetTime: Long?,
        size: Int,
    ): ApiResponseState = withContext(scope.coroutineContext) {
        val result = remoteManager.getReserveOrder(
            startTime,
            endTime,
            if (sportIds.size == 1 && sportIds.first() == -1) null else sportIds,
            if (matchId == -1L) null else matchId,
            cursorBetTime,
            size
        )
        return@withContext if (result.error == null && result.data != null) {
            val currency = infoDao.getCurrency()
            val data = result.data!!.orderList.map { it.toReserveOrderBean(currency, matchId) }
            betSlipReserveDao.insert(data)
            ApiResponseState.Succeeded(data)
        } else {
            betSlipReserveDao.deleteAllByMatchId(matchId)
            ApiResponseState.Failed(result.error)
        }
    }


    suspend fun reserveCancel(reserveId: String): Client.ReserveCancelResp? {
        return remoteManager.reserveCancelReq(reserveId).apply {
            if (this?.success == true) {
                betSlipReserveDao.deleteById(reserveId)
            }
        }
    }

    suspend fun reserveUpdate(
        reserveId: String,
        amount: Long,
        newOdds: Int
    ): Client.ReserveUpdateResp? {
        return remoteManager.reserveUpdateReq(reserveId, amount, newOdds).apply {
            if (this?.success == true) {
                betSlipReserveDao.updateOdds(reserveId, newOdds)
            }
        }
    }

    fun deleteAll(matchId: Long) {
        scope.launch {
            if (matchId == -1L) {
                betSlipReserveDao.deleteAll()
            } else {
                betSlipReserveDao.deleteAllByMatchId(matchId)
            }
        }
    }

}