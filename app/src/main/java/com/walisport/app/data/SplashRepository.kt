package com.walisport.app.data

import com.walisport.lib_base.data.repository.BaseRepository
import com.walisport.lib_base.utils.LogUtilsExt.loge
import com.walisport.lib_base.utils.LogUtilsExt.logi
import com.walisport.lib_common.helper.CountDownHelper
import com.walisport.lib_socket.WebSocketManager
import com.walisport.lib_socket.data.ApiCode
import com.walisport.lib_socket.data.ConnectState
import com.walisport.lib_socket.data.IResponse
import com.walisport.lib_socket.data.ResponseTimeOutError
import com.walisport.lib_socket.data.SocketResponseData
import com.walisport.lib_socket.data.SocketResponseError
import com.walisport.lib_socket.extension.asRemoteRequest
import com.walisport.lib_socket.extension.observeProtoMessage
import galaxy.client.proto.Client
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

class SplashRepository(
    override val scope: CoroutineScope,
    private val socketManager: WebSocketManager
) : BaseRepository() {

    private val countDownHelper = CountDownHelper()

    internal var countDown: Int by countDownHelper::countDown
    internal val countDownSecondsLD: SharedFlow<Int> by countDownHelper::countDownSecondsLD
    internal val isCountDownStart by countDownHelper::isCountDownStart

    init {
        countDown = 5_000
    }

    suspend fun startSocket() : ConnectState {
        return socketManager.connect("wss://betwavepro.ja700.com/fb-ws").first()
    }

    suspend fun sendLogin(uid: Int, token: String): IResponse {
        //要注意，一定要在同一個scope中
        val deferred = scope.async(Dispatchers.Default) {
            withTimeoutOrNull(3000) {
                socketManager.observeProtoMessage<Client.LoginResp>(ApiCode.LOGIN).first()
            }
        }
        scope.launch(Dispatchers.Default) {
            val req = Client.LoginReq.newBuilder().apply {
                this.uid = uid.toLong()
                this.token = token
                this.lang = "zh-CN"
                this.platform = 5
                this.oddType = 0
            }.build()
            socketManager.send(req.asRemoteRequest(ApiCode.LOGIN))
        }
        return deferred.await() ?: ResponseTimeOutError()
    }

}