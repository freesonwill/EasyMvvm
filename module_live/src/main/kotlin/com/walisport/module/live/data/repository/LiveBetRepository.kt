package com.walisport.module.live.data.repository

import arch.cayenne.lib.base.data.repository.BaseRepository
import com.walisport.module.live.LiveRemoteManager
import galaxy.client.proto.Client.EarlySettlePriceResp
import galaxy.client.proto.Client.EarlySettleResp
import galaxy.client.proto.Client.ReserveCancelResp
import galaxy.client.proto.Client.ReserveUpdateResp
import galaxy.common.proto.Common
import galaxy.common.proto.Common.EarlySettlePrice
import kotlinx.coroutines.CoroutineScope

class LiveBetRepository(
    override val scope: CoroutineScope,
    private val remoteManager: LiveRemoteManager
) : BaseRepository() {


    suspend fun getOrderReq(
        status: Int,
        page: Int,
        pageSize: Int,
        sportId: Int,
        matchId: Long,
        startTime: Long? = null,
        endTime: Long? = null,
    ): List<Common.Order>? {
        val resp = remoteManager.getOrderReq(
            scope,
            status,
            page,
            pageSize,
            sportId,
            matchId,
            startTime,
            endTime
        )
        return resp
    }


    suspend fun getReserveOrder(
        sportId: Int,
        matchId: Long,
        startTime: Long? = null,
        endTime: Long? = null
    ): List<Common.ReserveOrder>? {
        val resp = remoteManager.getReserveOrder(scope, sportId, matchId, startTime, endTime)
        return resp
    }

    suspend fun earlySettle(
        betId: String,
        amount: String,
        expectPrice: String,
        acceptPriceReduce: Boolean
    ): EarlySettleResp? {
        val resp =
            remoteManager.earlySettleReq(scope, betId, amount, expectPrice, acceptPriceReduce)
        return resp
    }

    suspend fun reserveCancel(reserveId: String): ReserveCancelResp? {
        val resp = remoteManager.reserveCancelReq(scope, reserveId)
        return resp
    }

    suspend fun reserveUpdate(
        amount: String,
        odds: String
    ): ReserveUpdateResp? {
        val resp = remoteManager.reserveUpdateReq(scope, amount, odds)
        return resp
    }

    suspend fun earlySettledPrice(betId: String): List<EarlySettlePrice>? {
        val resp = remoteManager.earlySettlePriceReq(scope, betId)
        return resp?.priceList
    }

}