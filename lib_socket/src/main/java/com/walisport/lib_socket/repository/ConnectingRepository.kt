package com.walisport.lib_socket.repository

import com.google.protobuf.Api
import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.common.data.UserDataKey
import arch.cayenne.lib.common.data.UserDataManager
import com.walisport.lib_socket.WebSocketManager
import com.walisport.lib_socket.data.ApiCode
import com.walisport.lib_socket.data.LoginTokenFailedError
import com.walisport.lib_socket.data.SocketResponseData
import com.walisport.lib_socket.extension.sendAndWaitProtoMessageResponse
import galaxy.client.proto.Client
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class ConnectingRepository(
    override val scope: CoroutineScope,
    private val socketManager: WebSocketManager,
    private val userDataManager: UserDataManager
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