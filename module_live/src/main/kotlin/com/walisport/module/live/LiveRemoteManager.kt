package com.walisport.module.live

import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.socket.WebSocketManager
import arch.cayenne.lib.socket.data.ApiCode
import arch.cayenne.lib.socket.extension.sendAndWaitProtoMessageResponse
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
import galaxy.client.proto.Sloth
import galaxy.common.proto.Common
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class LiveRemoteManager(private val socketManager: WebSocketManager) {
    private val TAG = LiveRemoteManager::class.java.simpleName

    suspend fun queryLiveStream(
        scope: CoroutineScope,
        matchId: Long
    ): List<Sloth.MatchLiveStream>? {
        val res = socketManager.sendAndWaitProtoMessageResponse<Client.MatchLiveStreamResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.MATCH_LIVE_STREAM,
        ) {
            Client.MatchLiveStreamReq.newBuilder().apply {
                this.matchId = matchId
            }.build()
        }
        return if (res.error == null && res.data != null) {
            val data = res.data!!

            data.streamsList
        } else {
            null
        }
    }

    //获取阵容实时数据
    suspend fun getMatchLiveReq(scope: CoroutineScope, matchId: Long): Sloth.MatchLineupDetail? {
        val result = socketManager.sendAndWaitProtoMessageResponse<Client.MatchLineupResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.GET_LINEUP
        ) {
            Client.MatchLineupReq.newBuilder().apply {
                this.matchId = matchId
            }.build()
        }
        if (result.error == null && result.data != null) {
            return result.data!!.matchLineupDetail
        }
        return null
    }

    // 500-1003: 获取比赛详情
    suspend fun getMatchReq(scope: CoroutineScope, matchId: Long): Common.Match? {
        val result = socketManager.sendAndWaitProtoMessageResponse<Client.GetMatchResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.GET_MATCH
        ) {
            Client.GetMatchReq.newBuilder().apply {
                this.addMatchId(matchId)
            }.build()
        }
        if (result.error == null && result.data != null) {
            LogUtils.dTag("result", "matchMainMatchresult----->${result}")
            return result.data!!.matchList.find {
                it.matchId==matchId
            }
        }
        return null
    }


    suspend fun getOrderReq(
        scope: CoroutineScope,
        status: Int,
        page: Int,
        pageSize: Int,
        sportId: Int,
        matchId: Long,
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
                this.addSportId(sportId)
                this.matchId = matchId
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


    //获取比赛趋势的实时数据
    suspend fun getMatchTrendReq(scope: CoroutineScope, matchId: Long): Sloth.MatchTrendData? {
        val result = socketManager.sendAndWaitProtoMessageResponse<Client.MatchTrendResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.MATCH_TREND
        ) {
            Client.MatchTrendReq.newBuilder().apply {
                this.matchId = matchId
            }.build()
        }
        if (result.error == null && result.data != null) {
            return result.data!!.matchTrendData
        }
        return null
    }

    //获取积分榜的实时数据
    suspend fun getCompetitionReq(scope: CoroutineScope, compId: Int): Sloth.CompetitionTables? {
        val result = socketManager.sendAndWaitProtoMessageResponse<Client.CompetitionTableResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.GET_STANDINGS
        ) {
            Client.CompetitionTableReq.newBuilder().apply {
                this.compId = compId
            }.build()
        }
        if (result.error == null && result.data != null) {
            return result.data!!.competitionTables
        }
        return null
    }

    //获取联赛日程列表数据
    suspend fun getMatchLeagueReq(
        scope: CoroutineScope,
        tournamentId: Int
    ): Client.TournamentMatchResp? {
        val result = socketManager.sendAndWaitProtoMessageResponse<Client.TournamentMatchResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.MATCH_LEAGUE
        ) {
            Client.TournamentMatchReq.newBuilder().apply {
                this.tournamentId = tournamentId
                this.page = 1
                this.size = 50
            }.build()
        }
        if (result.error == null && result.data != null) {
            return result.data!!
        }
        return null
    }

    //获取比赛技术统计实时数据
    suspend fun getMatchStatisticReq(scope: CoroutineScope, matchId: Long): Sloth.MatchLiveData? {
        val result = socketManager.sendAndWaitProtoMessageResponse<Client.MatchLiveResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.MATCH_LIVE
        ) {
            Client.MatchLiveReq.newBuilder().apply {
                this.matchId = matchId
            }.build()
        }
        if (result.error == null && result.data != null) {
            return result.data!!.matchLiveData
        }
        return null
    }

    // 500-1007: 盘口分类
    suspend fun getMarketTypeReq(scope: CoroutineScope, matchId: Long): List<Common.MarketType>? {
        val result = socketManager.sendAndWaitProtoMessageResponse<Client.MarketTypeResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.GET_MARKET_TYPE
        ) {
            Client.MatchTrendReq.newBuilder().apply {
                this.matchId = matchId
            }.build()
        }
        if (result.error == null && result.data != null) {
            return result.data!!.marketTypeList
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