package arch.cayenne.module.order.data.repo

import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.websocket.WebSocketManager
import arch.cayenne.lib.websocket.data.ApiCode
import arch.cayenne.lib.websocket.extension.sendAndWaitProtoMessageResponse
import galaxy.client.proto.Client
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class GameRepository(
    override val scope: CoroutineScope,
    private val socketManager: WebSocketManager
) : BaseRepository() {

    suspend fun getMaxBonusList(): ApiResponseState {
        val res = socketManager.sendAndWaitProtoMessageResponse<Client.ListOutrightMatchResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.LIST_OUTRIGHT_MATCH,
        ) {
            Client.ListOutrightMatchReq.newBuilder().apply {
                this.sportId = sportId
            }.build()
        }
        if (res.error == null && res.data != null) {

        }
        return ApiResponseState.Failed(res.error)
    }

    suspend fun getMaxMultipleList(): ApiResponseState {
        val res = socketManager.sendAndWaitProtoMessageResponse<Client.ListOutrightMatchResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.LIST_OUTRIGHT_MATCH,
        ) {
            Client.ListOutrightMatchReq.newBuilder().apply {
                this.sportId = sportId
            }.build()
        }
        if (res.error == null && res.data != null) {

        }
        return ApiResponseState.Failed(res.error)
    }
}