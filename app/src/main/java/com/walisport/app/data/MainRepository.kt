package com.walisport.app.data

import com.walisport.lib_base.data.repository.BaseRepository
import com.walisport.lib_socket.WebSocketManager
import com.walisport.lib_socket.data.IConnectState
import com.walisport.lib_socket.data.SocketResponseError
import com.walisport.lib_socket.data.SocketResponseData
import com.walisport.lib_socket.extension.asRemoteRequest
import com.walisport.lib_socket.extension.observeProtoMessage
import galaxy.client.proto.Client
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

/**
 * @author: zhangsan
 * @date: 2025/3/14 16:58
 * @description:
 */
class MainRepository(private val socketManager: WebSocketManager) : BaseRepository() {

    suspend fun startSocket() : IConnectState? {
        return withTimeoutOrNull(5000) {
            async {
                socketManager.connect("wss://betwavepro.ja700.com/fb-ws").first()
            }.await()
        }
    }

    fun sendLogin(uid: Int, token: String) {
        scope.launch {
            val deferred = withTimeoutOrNull(2000) {
                async {
                    socketManager.observeProtoMessage<Client.LoginResp>(7,7).first()
                }.await()
            }
            if (deferred == null) {
              //time out

            } else if (deferred is SocketResponseData<*>) {
                //拿到資料

            } else if (deferred is SocketResponseError) {
                //發生錯誤

            }

        }

        val req = Client.LoginReq.newBuilder().apply {
            this.uid = uid.toLong()
            this.token = token
            this.lang = "zh-CN"
            this.platform = 5
            this.oddType = 0
        }.build()
        socketManager.send(req.asRemoteRequest(7,7))
    }
}