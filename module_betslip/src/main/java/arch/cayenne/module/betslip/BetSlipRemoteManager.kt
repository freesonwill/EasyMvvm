package arch.cayenne.module.betslip

import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.websocket.WebSocketManager
import arch.cayenne.lib.websocket.data.ApiCode
import arch.cayenne.lib.websocket.extension.sendAndWaitProtoMessageResponse
import com.google.gson.Gson
import galaxy.client.proto.Client
import galaxy.client.proto.Client.EarlySettlePriceReq
import galaxy.client.proto.Client.EarlySettlePriceResp
import galaxy.client.proto.Client.EarlySettleReq
import galaxy.client.proto.Client.EarlySettleResp
import galaxy.client.proto.Client.ReserveCancelReq
import galaxy.client.proto.Client.ReserveCancelResp
import galaxy.client.proto.Client.ReserveUpdateReq
import galaxy.client.proto.Client.ReserveUpdateResp
import galaxy.common.proto.Common
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class BetSlipRemoteManager(
    private val scope: CoroutineScope,
    private val socketManager: WebSocketManager
) {
    private val TAG = this.javaClass.simpleName

    suspend fun getOrderReq(
        scope: CoroutineScope,
        status: Int,
        page: Int,
        pageSize: Int,
        sportId: Int? = null,
        matchId: Long? = null,
        startTime: Long? = null,
        endTime: Long? = null,
    ): List<Common.Order>? {
        "getOrderReq params status $status   page $page pageSize $pageSize matchId $matchId sportId $sportId".logd(TAG)
        val result = socketManager.sendAndWaitProtoMessageResponse<Client.GetOrderResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.GET_ORDER
        ) {
            Client.GetOrderReq.newBuilder().apply {
                this.status = status
                this.page = page
                this.pageSize = pageSize
                sportId?.let { this.addSportId(sportId) }
                matchId?.let { this.matchId = matchId }
                startTime?.let { this.startTime = startTime }
                endTime?.let { this.endTime = endTime }
            }.build()
        }
        "getOrderReq result ${Gson().toJson(result)}".logd(TAG)
        if (result.error == null && result.data != null) {
            return result.data!!.orderList
        }
        return null
    }

    suspend fun getReserveOrder(
        scope: CoroutineScope,
        sportId: Int,
        matchId: Long,
        startTime: Long? = null,
        endTime: Long? = null
    ): List<Common.ReserveOrder>? {
        "getReserveOrder params matchId $matchId sportId $sportId".logd(TAG)
        val result = socketManager.sendAndWaitProtoMessageResponse<Client.GetReserveOrderResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.GER_RESERVE_ORDER
        ) {
            Client.GetReserveOrderReq.newBuilder().apply {
                this.matchId = matchId
                startTime?.let { this.startTime = it }
                endTime?.let { this.endTime = it }
                this.addSportId(sportId)
            }.build()
        }
        "getReserveOrder  result ${result.data?.orderList?.size}".logd(TAG)
        if (result.error == null && result.data != null) {
            return result.data!!.orderList
        }
        return null
    }

    suspend fun earlySettleReq(
        scope: CoroutineScope,
        betId: String,
        amount: String,
        expectPrice: String,
        acceptPriceReduce: Boolean
    ): EarlySettleResp? {
        "earlySettleReq parma betId $betId amout $amount expectprice $expectPrice ".logd(TAG)
        val result = socketManager.sendAndWaitProtoMessageResponse<EarlySettleResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.EARLY_SETTLE
        ) {
            EarlySettleReq.newBuilder().apply {
                this.betId = betId
                this.amount = amount
                this.expectPrice = expectPrice
                this.acceptPriceReduce = acceptPriceReduce
            }.build()
        }
        "earlySettleReq result ${Gson().toJson(result)}".logd(TAG)
        if (result.error == null && result.data != null) {
            return result.data
        }
        return null
    }

    suspend fun reserveCancelReq(scope: CoroutineScope, reserveId: String): ReserveCancelResp? {
        val result = socketManager.sendAndWaitProtoMessageResponse<ReserveCancelResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.RESERVE_CANCEL
        ) {
            ReserveCancelReq.newBuilder().apply {
                this.reserveId = reserveId
            }.build()
        }
        "reserveCancel reserveId $reserveId  \n result ${Gson().toJson(result)}".logd(TAG)
        if (result.error == null && result.data != null) {
            return result.data
        }
        return null
    }

    suspend fun reserveUpdateReq(
        scope: CoroutineScope,
        reserveId: String,
        amount: String,
        odds: String
    ): ReserveUpdateResp? {
        val result = socketManager.sendAndWaitProtoMessageResponse<ReserveUpdateResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.RESERVE_UPDATE
        ) {
            ReserveUpdateReq.newBuilder().apply {
                this.reserveId = reserveId
                this.amount = amount
                this.odds = odds
            }.build()
        }
        "reserveUpdateReq reserveId $reserveId amount $amount odds $odds  \n result ${Gson().toJson(result)}".logd(TAG)
        if (result.error == null && result.data != null) {
            return result.data
        }
        return null
    }

    suspend fun earlySettlePriceReq(scope: CoroutineScope, betId: String): EarlySettlePriceResp? {
        val result = socketManager.sendAndWaitProtoMessageResponse<EarlySettlePriceResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.EARLY_SETTLE_PRICE
        ) {
            EarlySettlePriceReq.newBuilder().apply {
                addBetId(betId)
            }.build()
        }
        "betId $betId earlySettlePrice  ${Gson().toJson(result)}".logd(TAG)
        if(result.error == null && result.data != null){
            return result.data
        }
        return null
    }

}