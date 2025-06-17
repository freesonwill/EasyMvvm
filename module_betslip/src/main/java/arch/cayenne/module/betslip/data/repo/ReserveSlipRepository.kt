package arch.cayenne.module.betslip.data.repo

import arch.cayenne.lib.database.dao.BetSlipReserveDao
import arch.cayenne.lib.database.entity.BetSlipReserveBean
import arch.cayenne.module.betslip.BetSlipRemoteManager
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
    ): List<BetSlipReserveBean>? {
        return withContext(scope.coroutineContext) {
            remoteManager.getReserveOrder(
                startTime,
                endTime,
                if (sportIds.size == 1 && sportIds.first() == -1) null else sportIds,
                if (matchId == -1L) null else matchId,
                cursorBetTime,
                size
            ).apply {
                if (this.isNullOrEmpty()) {
                    betSlipReserveDao.deleteAll()
                } else {
                    betSlipReserveDao.insert(this)
                    betSlipReserveDao.deleteMissing(this.map { it.reserveId })
                }
            }
        }
    }

    suspend fun loadMoreReserveOrder(
        startTime: Long?,
        endTime: Long?,
        sportIds: List<Int>,
        matchId: Long,
        cursorBetTime: Long?,
        size: Int,
    ): List<BetSlipReserveBean>? {
        return withContext(scope.coroutineContext) {
            remoteManager.getReserveOrder(
                startTime,
                endTime,
                if (sportIds.size == 1 && sportIds.first() == -1) null else sportIds,
                if (matchId == -1L) null else matchId,
                cursorBetTime,
                size
            ).apply {
                if (this == null) {
                    betSlipReserveDao.deleteAll()
                } else {
                    betSlipReserveDao.insert(this)
                }
            }
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