package com.walisport.module.live.data

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.socket.WebSocketManager
import arch.cayenne.lib.socket.data.ApiCode
import arch.cayenne.lib.socket.data.SocketResponseData
import arch.cayenne.lib.socket.extension.sendAndWaitProtoMessageResponse
import com.walisport.module.live.LiveRemoteManager
import galaxy.client.proto.Client
import galaxy.client.proto.Client.EarlySettlePriceResp
import galaxy.client.proto.Client.EarlySettleResp
import galaxy.client.proto.Client.ReserveCancelResp
import galaxy.client.proto.Client.ReserveUpdateResp
import galaxy.common.proto.Common
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

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

    suspend fun earlySettlePrice(
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

}