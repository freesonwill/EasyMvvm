package arch.cayenne.module.betslip.data.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.module.betslip.BetSlipRemoteManager
import galaxy.client.proto.Client.EarlySettleResp
import galaxy.client.proto.Client.ReserveCancelResp
import galaxy.client.proto.Client.ReserveUpdateResp
import galaxy.common.proto.Common
import galaxy.common.proto.Common.EarlySettlePrice
import kotlinx.coroutines.CoroutineScope

class BetSlipRepository(
    override val scope: CoroutineScope,
    private val remoteManager: BetSlipRemoteManager
) : BaseRepository() {


    suspend fun getOrderReq(
        status: Int,
        startTime: Long?,
        endTime: Long?,
        cursorBetTime: Long,
        size: Int,
        sportId: Int,
        matchId: Long,
    ): List<Common.Order>? {
        return remoteManager.getOrderReq(
            status,
            startTime,
            endTime,
            cursorBetTime,
            size,
            if (sportId == -1) null else sportId,
            if (matchId == -1L) null else matchId,

            )
    }


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

    suspend fun earlySettle(
        betId: String,
        amount: String,
        expectPrice: String,
        acceptPriceReduce: Boolean
    ): EarlySettleResp? {
        return remoteManager.earlySettleReq(betId, amount, expectPrice, acceptPriceReduce)
    }

    suspend fun reserveCancel(reserveId: String): ReserveCancelResp? {
        return remoteManager.reserveCancelReq(reserveId)
    }

    suspend fun reserveUpdate(
        reserveId: String,
        amount: String,
        odds: String
    ): ReserveUpdateResp? {
        return remoteManager.reserveUpdateReq(reserveId, amount, odds)
    }

    suspend fun earlySettledPrice(betId: String): List<EarlySettlePrice>? {
        val resp = remoteManager.earlySettlePriceReq(betId)
        return resp?.priceList
    }

}