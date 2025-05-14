package com.walisport.module.live

import arch.cayenne.lib.base.utils.LogUtils
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
                this.matchId=matchId
            }.build()
        }
        if (result.error == null && result.data != null) {
            LogUtils.dTag("result", "matchMainMatchresult----->${result}")
            return  result.data!!.match
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
        tournamentId: Int,
        page: Int
    ): Client.TournamentMatchResp? {
        val result = socketManager.sendAndWaitProtoMessageResponse<Client.TournamentMatchResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.MATCH_LEAGUE
        ) {
            Client.TournamentMatchReq.newBuilder().apply {
                this.tournamentId = tournamentId
                this.page = page
                this.size = 20
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

}