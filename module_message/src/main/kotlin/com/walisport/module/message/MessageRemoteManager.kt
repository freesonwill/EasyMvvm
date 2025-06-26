package com.walisport.module.message

import arch.cayenne.lib.base.utils.ext.LogUtilsExt.loge
import arch.cayenne.lib.websocket.WebSocketManager
import arch.cayenne.lib.websocket.data.ApiCode
import arch.cayenne.lib.websocket.extension.sendAndWaitProtoMessageResponse
import galaxy.client.proto.Client
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class MessageRemoteManager(private val socketManager: WebSocketManager) {

    //获取阵容实时数据
    suspend fun getUserMessageListReq(
        scope: CoroutineScope,
        id: Long,
        type: Int
    ): Client.GetSystemMsgResp? {
        val result = socketManager.sendAndWaitProtoMessageResponse<Client.GetSystemMsgResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.USER_SYS_MESSAGE
        ) {
            Client.GetSystemMsgReq.newBuilder().apply {
                this.id = id.toInt() //消息ID 最后一条
                this.size = 10        //每页展示的数量
                this.type = type      //消息类型 0.全部 1.系统通知  2.活动通知
            }.build()
        }
        if (result.error == null && result.data != null) {
            return result.data!!
        }
        return null
    }

    //修改消息状态，删除或将消息设为已读
    suspend fun updateMessageStatus(
        scope: CoroutineScope,
        id: Long,
        status: Int
    ): Client.SystemMsgStatusUpdateResp? {
        val result =
            socketManager.sendAndWaitProtoMessageResponse<Client.SystemMsgStatusUpdateResp>(
                scope = scope,
                dispatcher = Dispatchers.IO,
                apiCode = ApiCode.UPDATE_MESSAGE
            ) {
                Client.SystemMsgStatusReq.newBuilder().apply {
                    this.id = id.toInt()  //消息ID
                    this.status = status  //1未读 2已读 3删除
                }.build()
            }
        if (result.error == null && result.data != null) {
            return result.data!!
        }
        return null
    }
}