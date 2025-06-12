package com.walisport.module.live

import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.websocket.WebSocketManager
import arch.cayenne.lib.websocket.data.ApiCode
import arch.cayenne.lib.websocket.extension.observeProtoMessage
import arch.cayenne.lib.websocket.extension.sendAndWaitProtoMessageResponse
import galaxy.client.proto.Client
import galaxy.client.proto.Sloth
import galaxy.common.proto.Common
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import okio.utf8Size

class LiveRemoteManager(private val socketManager: WebSocketManager) {

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
                this.matchId = matchId
            }.build()
        }
        if (result.error == null && result.data != null) {
            LogUtils.dTag("result", "matchMainMatchResult----->${result.toString()}")
            return result.data!!.match
        }
        return null
    }

    // 500-1102: 订阅比赛详情
    suspend fun registerMatchInfoNotify(scope: CoroutineScope, matchIds: Long) {
        socketManager.sendAndWaitProtoMessageResponse<Client.SubscribeMatchInfoResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.SUBSCRIBE_MATCH_INFO,
        ) {
            Client.SubscribeMatchInfoReq.newBuilder().apply {
                this.matchId = matchIds
            }.build()
        }
    }

    // 500-1103: 取消订阅比赛详情
    fun unregisterMatchInfoNotify(scope: CoroutineScope, matchIds: Long) {
        scope.launch {
            socketManager.sendAndWaitProtoMessageResponse<Client.CancelSubscribeMatchInfoResp>(
                scope = scope,
                dispatcher = Dispatchers.IO,
                apiCode = ApiCode.CANCEL_SUBSCRIBE_MATCH_INFO,
            ) {
                Client.CancelSubscribeMatchInfoReq.newBuilder().apply {
                    this.matchId = matchIds
                }.build()
            }
        }
    }

    // 600-1004: 比赛INFO推送
    fun observeMatchInfoNotify(): Flow<Client.MatchInfoNotify> {
        return socketManager.observeProtoMessage<Client.MatchInfoNotify>(ApiCode.MATCH_INFO_NOTIFY)
            .transform { res ->
                if (res.error == null && res.data != null) {
                    emit(res.data!!)
                }
            }
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
        cursorMatchId: Long,
        cursorMatchStartTime: Long
    ): Client.TournamentMatchResp? {
        val result = socketManager.sendAndWaitProtoMessageResponse<Client.TournamentMatchResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.MATCH_LEAGUE
        ) {
            Client.TournamentMatchReq.newBuilder().apply {
                this.tournamentId = tournamentId
                this.cursorMatchId = cursorMatchId
                this.cursorMatchStartTime = cursorMatchStartTime
                this.size = 10
            }.build()
        }
        if (result.error == null && result.data != null) {
            return result.data!!
        }
        return null
    }

    // 500-1007: 盘口分类
    suspend fun getMarketTypeReq(scope: CoroutineScope, matchId: Long): List<Common.MarketCategory>? {
        val result = socketManager.sendAndWaitProtoMessageResponse<Client.MarketCategoryResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.GET_MARKET_TYPE
        ) {
            Client.MarketCategoryReq.newBuilder().apply {
                this.matchId = matchId
            }.build()
        }
        if (result.error == null && result.data != null) {
            LogUtils.dTag("盘口分类","盘口分类----${result.data.toString()}")
            return result.data!!.marketCategoryList
        }
        return null
    }

    //700-1100: 订阅比赛统计数据推送
    suspend fun registerMatchStaticsNotify(
        scope: CoroutineScope,
        matchIds: Long
    ): Sloth.MatchLiveData? {
        val result = socketManager.sendAndWaitProtoMessageResponse<Client.SubscribeMatchLiveResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.MATCH_STATICS,
        ) {
            Client.SubscribeMatchLiveReq.newBuilder().apply {
                this.matchId = matchIds
            }.build()
        }
        if (result.error == null && result.data != null) {
            return result.data!!.matchLiveData
        }
        return null
    }

    //700-1100: 接收比赛统计数据推送(订阅和接收技术统计，apiCode都为700-1100)
    fun observeMatchStaticsNotify(): Flow<Sloth.MatchLiveData> {
        return socketManager.observeProtoMessage<Client.SubscribeMatchLiveResp>(ApiCode.MATCH_STATICS)
            .transform { res ->
                if (res.error == null && res.data != null) {
                    emit(res.data!!.matchLiveData)
                }
            }
    }
}