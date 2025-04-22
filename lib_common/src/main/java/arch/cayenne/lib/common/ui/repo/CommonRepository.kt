package arch.cayenne.lib.common.ui.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.common.data.UserDataKey
import arch.cayenne.lib.common.data.UserDataManager
import arch.cayenne.lib.socket.WebSocketManager
import arch.cayenne.lib.socket.data.ApiCode
import arch.cayenne.lib.socket.data.LoginTokenFailedError
import arch.cayenne.lib.socket.data.SocketResponseData
import arch.cayenne.lib.socket.extension.sendAndWaitProtoMessageResponse
import galaxy.client.proto.Client
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class CommonRepository(
    override val scope: CoroutineScope,
    private val socketManager: WebSocketManager,
    private val userDataManager: UserDataManager,
    private val 
) : BaseRepository() {

    fun getConnectStateFlow() = socketManager.getConnectStateFlow()

    suspend fun sendLogin(): SocketResponseData<Client.LoginResp> {
        val uid = userDataManager.getIntValue(UserDataKey.KEY_UID, -1)
        val token = userDataManager.getStringValue(UserDataKey.KEY_TOKEN, "")
        //沒有Token
        if (uid == -1 || token == "") {
            return SocketResponseData(
                mid = ApiCode.LOGIN.mid,
                sid = ApiCode.LOGIN.sid,
                data = null,
                error = LoginTokenFailedError()
            )
        }

        return socketManager.sendAndWaitProtoMessageResponse<Client.LoginResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.LOGIN
        ) {
            Client.LoginReq.newBuilder().apply {
                this.uid = uid.toLong()
                this.token = token
                this.lang = "zh-CN"
                this.platform = 5
                this.oddType = 0
            }.build()
        }
    }

    fun reset() {
        socketManager.reset()
    }
}