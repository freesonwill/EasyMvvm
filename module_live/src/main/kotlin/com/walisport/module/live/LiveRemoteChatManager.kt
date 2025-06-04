package com.walisport.module.live

import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.websocket.chat.ChatWebSocketManager
import arch.cayenne.lib.websocket.chat.data.ChatLoginRequestData
import arch.cayenne.lib.websocket.chat.data.ChatLoginResponseData
import arch.cayenne.lib.websocket.chat.data.ChatResponseCode
import arch.cayenne.lib.websocket.chat.extension.chatSendAndWaitProtoMessageResponse
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.websocket.data.ApiCode
import arch.cayenne.lib.websocket.data.ConnectState
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import org.koin.java.KoinJavaComponent.inject

class LiveRemoteChatManager(
    private val socketManager: ChatWebSocketManager,
    private val userDataManager: UserDataManager
) {
    private val TAG = this.javaClass.simpleName

    suspend fun startSocket(): ConnectState {
        val state = socketManager.getConnectStateFlow().firstOrNull()
        if (state == ConnectState.ConnectSuccess) {
            return state
        }
        return socketManager.connect("wss://ws.qxe68.com:7001/api/game/chat/ws").first()
    }

    suspend fun disConnect(): Boolean {
        return socketManager.disconnect()
    }


    suspend fun login(scope: CoroutineScope): ChatLoginResponseData? {
        val uid = userDataManager.getValue(UserDataKey.KEY_UID, -1)
        val token = userDataManager.getValue(UserDataKey.KEY_TOKEN, "")

        val logResp = socketManager.chatSendAndWaitProtoMessageResponse<ChatLoginResponseData>(
            scope,
            Dispatchers.IO,
            ApiCode.CHAT_LOGIN,
            responseCode = ChatResponseCode.LOGIN
        ) {
            ChatLoginRequestData(uid.toLong(), token, 5)
        }

        if (logResp.error != null && logResp.data != null) {
            return logResp.data
        }
        "login uid:$uid  token:$token   result ${Gson().toJson(logResp)}".logd(TAG)
        return null
    }

}