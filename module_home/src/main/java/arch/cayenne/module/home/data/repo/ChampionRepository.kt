package arch.cayenne.module.home.data.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logi
import arch.cayenne.lib.websocket.WebSocketManager
import arch.cayenne.lib.websocket.data.ApiCode
import arch.cayenne.lib.websocket.extension.sendAndWaitProtoMessageResponse
import galaxy.client.proto.Client
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class ChampionRepository(
    override val scope: CoroutineScope,
    private val socketManager: WebSocketManager,
) : BaseRepository() {

    suspend fun getChampionDetail(matchId: Long) {
        val resp = socketManager.sendAndWaitProtoMessageResponse<Client.GetMatchResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.GET_MATCH,
        ){
            Client.GetMatchReq.newBuilder().apply {
                this.matchId = matchId
            }.build()
        }

        if (resp.error == null && resp.data != null) {
            "KC_ ${resp.data!!.match.marketList}".logi()

        }
    }
}