package com.walisport.module.live

import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.socket.WebSocketManager
import arch.cayenne.lib.socket.data.ApiCode
import arch.cayenne.lib.socket.extension.sendAndWaitProtoMessageResponse
import galaxy.client.proto.Client
import galaxy.client.proto.Sloth
import galaxy.common.proto.Common
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class LiveRemoteManager(private val socketManager: WebSocketManager) {

    suspend fun queryLiveStream(scope: CoroutineScope, matchId: Long): List<Sloth.MatchLiveStream>? {
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
    suspend fun getMatchLiveReq(scope: CoroutineScope, matchId: Long ): Sloth.MatchLineupDetail? {
        val result = socketManager.sendAndWaitProtoMessageResponse<Client.MatchLineupResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.GET_LINEUP
        ) {
            Client.MatchLineupReq.newBuilder().apply {
                this.matchId = matchId
            }.build()
        }
        if(result.error == null && result.data != null){
            return result.data!!.matchLineupDetail
        }
        return null
    }

    // 500-1003: 获取比赛详情
    suspend fun getMatchReq(scope: CoroutineScope, matchId: Long ): List<Common.Match>? {
        val result = socketManager.sendAndWaitProtoMessageResponse<Client.GetMatchResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.GET_MATCH
        ) {
            Client.GetMatchReq.newBuilder().apply {
                this.addMatchId(matchId)
            }.build()
        }
        if(result.error == null && result.data != null){
            LogUtils.dTag("result", "matchMainMatchresult----->${result}")
            return result.data!!.matchList
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
        LogUtils.dTag("aaa","getOrderReq status $status   page $page pageSize $pageSize matchId $matchId sportId $sportId")
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
        LogUtils.dTag("aaa", "result ${result?.data?.orderList?.size}")
        if(result.error == null && result.data != null){
            return result.data!!.orderList
        }
        LogUtils.dTag("aaa","error  ${result.error?.msg}")

        return null
    }


    suspend fun getReserveOrder(
        scope: CoroutineScope,
        sportId: Int,
        matchId: Long,
        startTime: Long? = null,
        endTime: Long? = null
    ): List<Common.ReserveOrder>? {
        LogUtils.dTag("aaa","getReserveOrder matchId $matchId sportId $sportId")

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
        LogUtils.dTag("aaa", " getReserveOrder  result ${result.data?.orderList?.size}   ${result.error != null} ${result?.data != null}")
        if (result.error == null && result.data != null) {
            return result.data!!.orderList
        }
        return null
    }


    //获取比赛趋势的实时数据
    suspend fun getMatchTrendReq(scope: CoroutineScope, matchId: Long ): Sloth.MatchTrendData? {
        val result = socketManager.sendAndWaitProtoMessageResponse<Client.MatchTrendResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.MATCH_TREND
        ) {
            Client.MatchTrendReq.newBuilder().apply {
                this.matchId = matchId
            }.build()
        }
        if(result.error == null && result.data != null){
            return result.data!!.matchTrendData
        }
        return null
    }

    //获取积分榜的实时数据
    suspend fun getCompetitionReq(scope: CoroutineScope, matchId: Long ): Sloth.CompetitionTables? {
        val result = socketManager.sendAndWaitProtoMessageResponse<Client.CompetitionTableResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.GET_STANDINGS
        ) {
            Client.MatchTrendReq.newBuilder().apply {
                this.matchId = matchId
            }.build()
        }
        if(result.error == null && result.data != null){
            return result.data!!.competitionTables
        }
        return null
    }
}