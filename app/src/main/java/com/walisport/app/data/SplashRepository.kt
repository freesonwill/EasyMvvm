package com.walisport.app.data

import com.walisport.lib.base.data.repository.BaseRepository
import com.walisport.lib.common.data.UserDataKey
import com.walisport.lib.common.data.UserDataManager
import com.walisport.lib.common.helper.CountDownHelper
import com.walisport.lib_socket.WebSocketManager
import com.walisport.lib_socket.data.ApiCode
import com.walisport.lib_socket.data.ConnectState
import com.walisport.lib_socket.data.SocketResponseData
import com.walisport.lib_socket.extension.sendAndWaitProtoMessageResponse
import galaxy.client.proto.Client
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.first

class SplashRepository(
    override val scope: CoroutineScope,
    private val socketManager: WebSocketManager,
    private val userDataManager: UserDataManager
) : BaseRepository() {

    private val countDownHelper = CountDownHelper()

    internal var countDown: Int by countDownHelper::countDown
    internal val countDownSecondsLD: SharedFlow<Int> by countDownHelper::countDownSecondsLD
    internal val isCountDownStart by countDownHelper::isCountDownStart

    init {
        countDown = 5_000
    }

    fun saveUserData(uid: Int, token: String) {
        userDataManager.setKeyValue(UserDataKey.KEY_UID, uid)
        userDataManager.setKeyValue(UserDataKey.KEY_TOKEN, token)
    }

    suspend fun startSocket() : ConnectState {
        return socketManager.connect("wss://betwavepro.ja700.com/fb-ws").first()
    }

    suspend fun sendLogin(uid: Int, token: String): SocketResponseData<Client.LoginResp> {
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
}