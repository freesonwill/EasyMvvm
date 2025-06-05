package arch.cayenne.module.betslip.data.repo

import arch.cayenne.module.betslip.BetSlipRemoteManager
import galaxy.client.proto.Client
import galaxy.common.proto.Common
import kotlinx.coroutines.CoroutineScope

class ReserveSlipRepository(
    scope: CoroutineScope,
    remoteManager: BetSlipRemoteManager
): BaseBetSlipRepository(scope, remoteManager) {

    suspend fun getReserveOrder(
        startTime: Long?,
        endTime: Long?,
        sportId: Int,
        matchId: Long,
        cursorBetTime: Long?,
        size: Int,
    ): List<Common.ReserveOrder>? {
        return remoteManager.getReserveOrder(
            startTime,
            endTime,
            sportId,
            matchId,
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