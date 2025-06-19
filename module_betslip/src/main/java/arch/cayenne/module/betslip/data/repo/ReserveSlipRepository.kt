package arch.cayenne.module.betslip.data.repo

import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.database.dao.BetSlipReserveDao
import arch.cayenne.module.betslip.BetSlipRemoteManager
import arch.cayenne.module.betslip.data.constants.CommonExtension.toReserveOrderBean
import galaxy.client.proto.Client
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReserveSlipRepository(
    scope: CoroutineScope,
    private val betSlipReserveDao: BetSlipReserveDao,
    remoteManager: BetSlipRemoteManager
) : BaseBetSlipRepository(scope, remoteManager) {

    fun observeReserveBean() = betSlipReserveDao.observeReserveBean()

    suspend fun getReserveOrder(
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
            val data = result.data!!.orderList.map { it.toReserveOrderBean() }
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

    suspend fun loadMoreReserveOrder(
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
            val data = result.data!!.orderList.map { it.toReserveOrderBean() }
            betSlipReserveDao.insert(data)
            ApiResponseState.Succeeded(data)
        } else {
            betSlipReserveDao.deleteAll()
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
        amount: String,
        newOdds: String
    ): Client.ReserveUpdateResp? {
        return remoteManager.reserveUpdateReq(reserveId, amount, newOdds).apply {
            if (this?.success == true) {
                betSlipReserveDao.updateOdds(reserveId, newOdds)
            }
        }
    }

    fun deleteAll() {
        scope.launch {
            betSlipReserveDao.deleteAll()
        }
    }

}