package com.walisport.module.live

import arch.cayenne.lib.socket.WebSocketManager
import arch.cayenne.lib.socket.data.ApiCode
import arch.cayenne.lib.socket.extension.sendAndWaitProtoMessageResponse
import galaxy.client.proto.Client
import galaxy.client.proto.Sloth
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
                this.matchId = matchId.toInt()
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
                this.matchId = matchId.toInt()
            }.build()
        }
        if(result.error == null && result.data != null){
            return result.data!!.matchLineupDetail
        }
        return null
    }

}