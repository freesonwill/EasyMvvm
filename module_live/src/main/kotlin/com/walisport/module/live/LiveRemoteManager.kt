package com.walisport.module.live

import arch.cayenne.lib.socket.WebSocketManager
import arch.cayenne.lib.socket.data.ApiCode
import arch.cayenne.lib.socket.extension.sendAndWaitProtoMessageResponse
import com.walisport.module.live.data.model.MatchLiveStreamBean
import galaxy.client.proto.Client
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class LiveRemoteManager(private val socketManager: WebSocketManager) {

    suspend fun queryLiveStream(scope: CoroutineScope, matchId: Int): List<MatchLiveStreamBean>? {
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


            data.streamsList.map {
                MatchLiveStreamBean(
                    name = it.name,
                    urlSource = it.urlSource,
                    streamType = it.streamType,
                    rtmpUrl = it.rtmpUrl,
                    m3U8Url = it.m3U8Url,
                    flvUrl = it.flvUrl,
                    language = it.language
                )
            }
        } else {
            null
        }
    }


}