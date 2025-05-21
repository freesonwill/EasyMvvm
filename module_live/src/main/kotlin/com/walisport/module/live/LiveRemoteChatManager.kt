package com.walisport.module.live

import GameChat
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.chatwebsocket.ChatWebSocketManager
import arch.cayenne.lib.chatwebsocket.data.ChatLoginRequestData
import arch.cayenne.lib.chatwebsocket.data.ChatLoginResponseData
import arch.cayenne.lib.chatwebsocket.extension.chatSendAndWaitProtoMessageResponse
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.websocket.data.ApiCode
import arch.cayenne.lib.websocket.data.ConnectState
import arch.cayenne.lib.websocket.extension.sendAndWaitProtoMessageResponse
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import org.koin.java.KoinJavaComponent.inject

class LiveRemoteChatManager(
    val socketManager: ChatWebSocketManager,
) {
    private val userDataManager: UserDataManager by inject(UserDataManager::class.java)

    suspend fun startSocket(): ConnectState {
        return socketManager.connect("wss://ws.qxe68.com:7001/api/game/chat/ws").first()
    }

    suspend fun disConnect(): Boolean {
        return socketManager.disconnect()
    }


    suspend fun login(scope: CoroutineScope) {
        val uid = userDataManager.getValue(UserDataKey.KEY_UID, -1)
        val token = userDataManager.getValue(UserDataKey.KEY_TOKEN, "")

        val logResp = socketManager.chatSendAndWaitProtoMessageResponse<ChatLoginResponseData>(
            scope,
            Dispatchers.IO,
            ApiCode.CHAT_LOGIN
        ) {
            ChatLoginRequestData(uid.toLong(), token, 5)
        }
        "ChatSocketClientService login uid:$uid  token:$token   request ${Gson().toJson(logResp)}".logd()
    }

}