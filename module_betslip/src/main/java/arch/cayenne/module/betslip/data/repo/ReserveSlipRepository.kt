package arch.cayenne.module.betslip.data.repo

import arch.cayenne.module.betslip.BetSlipRemoteManager
import arch.cayenne.module.betslip.data.model.ReserveOrderBean
import galaxy.client.proto.Client
import kotlinx.coroutines.CoroutineScope

class ReserveSlipRepository(
    scope: CoroutineScope,
    remoteManager: BetSlipRemoteManager
): BaseBetSlipRepository(scope, remoteManager) {

    suspend fun getReserveOrder(
        startTime: Long?,
        endTime: Long?,
        sportIds: List<Int>,
        matchId: Long,
        cursorBetTime: Long?,
        size: Int,
    ): List<ReserveOrderBean>? {
        return remoteManager.getReserveOrder(
            startTime,
            endTime,
            if (sportIds.size == 1 && sportIds.first() == -1) null else sportIds,
            if (matchId == -1L) null else matchId,
            cursorBetTime,
            size
        )
    }



    suspend fun reserveCancel(reserveId: String): Client.ReserveCancelResp? {
        return remoteManager.reserveCancelReq(reserveId)
    }

    suspend fun reserveUpdate(
        reserveId: String,
        amount: String,
        odds: String
    ): Client.ReserveUpdateResp? {
        return remoteManager.reserveUpdateReq(reserveId, amount, odds)
    }

}