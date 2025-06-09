package arch.cayenne.module.betslip

import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.websocket.WebSocketManager
import arch.cayenne.lib.websocket.data.ApiCode
import arch.cayenne.lib.websocket.extension.sendAndWaitProtoMessageResponse
import arch.cayenne.module.betslip.data.model.OrderBean
import arch.cayenne.module.betslip.data.model.ReserveOrderBean
import arch.cayenne.module.betslip.data.model.toOrderBean
import arch.cayenne.module.betslip.data.model.toReserveOrderBean
import com.google.gson.Gson
import galaxy.client.proto.Client
import galaxy.client.proto.Client.EarlySettlePriceReq
import galaxy.client.proto.Client.EarlySettlePriceResp
import galaxy.client.proto.Client.EarlySettleReq
import galaxy.client.proto.Client.EarlySettleResp
import galaxy.client.proto.Client.ListSportReq
import galaxy.client.proto.Client.ListSportResp
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
        status: Int,
        startTime: Long?,
        endTime: Long?,
        cursorBetTime: Long?,
        size: Int,
        sportIds: List<Int>?,
        matchId: Long?,
    ): List<OrderBean>? {
        "getOrderReq params status $status startTime $startTime endTime $endTime cursorBetTime $cursorBetTime size $size sportIds $sportIds matchId $matchId".logd(TAG)
        val result = socketManager.sendAndWaitProtoMessageResponse<Client.GetOrderResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.GET_ORDER
        ) {
            Client.GetOrderReq.newBuilder().apply {
                this.status = status
                this.size = size
                startTime?.let { this.startTime = it }
                endTime?.let { this.endTime = it }
                cursorBetTime?.let { this.cursorBetTime = it }
                sportIds?.let { this.addAllSportId(it) }
                matchId?.let { this.matchId = it }

            }.build()
        }
        "getOrderReq result ${Gson().toJson(result)}".logd(TAG)
        if (result.error == null && result.data != null) {
            return result.data!!.orderList.map { it.toOrderBean() }
        }
        return null
    }

    suspend fun getReserveOrder(
        startTime: Long?,
        endTime: Long?,
        sportId: List<Int>?,
        matchId: Long?,
        cursorBetTime: Long?,
        size: Int
    ): List<ReserveOrderBean>? {
        "getReserveOrder params startTime $startTime endTime $endTime sportId $sportId matchId $matchId cursorBetTime $cursorBetTime size $size".logd(TAG)
        val result = socketManager.sendAndWaitProtoMessageResponse<Client.GetReserveOrderResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.GER_RESERVE_ORDER
        ) {
            Client.GetReserveOrderReq.newBuilder().apply {
                startTime?.let { this.startTime = it }
                endTime?.let { this.endTime = it }
                sportId?.let { this.addAllSportId(it) }
                matchId?.let { this.matchId = it }
                cursorBetTime?.let { this.cursorBetTime = it }
                this.size = size
            }.build()
        }
        "getReserveOrder  result ${result.data?.orderList?.size}".logd(TAG)
        if (result.error == null && result.data != null) {
            return result.data!!.orderList.map { it.toReserveOrderBean() }
        }
        return null
    }

    suspend fun earlySettleReq(
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

    suspend fun reserveCancelReq(reserveId: String): ReserveCancelResp? {
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

    suspend fun earlySettlePriceReq(betId: String): EarlySettlePriceResp? {
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

    suspend fun getSportList(): List<Common.Sport> {
        val result = socketManager.sendAndWaitProtoMessageResponse<ListSportResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.LIST_SPORT
        ) {
            ListSportReq.newBuilder().build()
        }
        if(result.error == null && result.data != null){
            return result.data!!.sportList
        }
        return emptyList()
    }
}