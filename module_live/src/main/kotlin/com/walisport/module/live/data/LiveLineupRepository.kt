package com.walisport.module.live.data

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.socket.WebSocketManager
import arch.cayenne.lib.socket.data.ApiCode
import arch.cayenne.lib.socket.extension.sendAndWaitProtoMessageResponse
import galaxy.client.proto.Client
import galaxy.client.proto.Sloth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class LiveLineupRepository(
    override val scope: CoroutineScope,
    private val socketManager: WebSocketManager
) : BaseRepository() {
    //获取阵容实时数据
    suspend fun getMatchLiveReq( matchId: Long ): Sloth.MatchLineupDetail? {
        val result = socketManager.sendAndWaitProtoMessageResponse<Client.MatchLineupResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.GET_LINEUP
        ) {
            Client.MatchLineupReq.newBuilder().apply {
                this.matchId = matchId.toInt()
            }.build()
        }
        if(result.error != null && result.data != null){
            return result.data!!.matchLineupDetail
        }
        return null
    }
}