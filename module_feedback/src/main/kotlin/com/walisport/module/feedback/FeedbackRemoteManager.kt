package com.walisport.module.feedback

import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.websocket.WebSocketManager
import arch.cayenne.lib.websocket.data.ApiCode
import arch.cayenne.lib.websocket.extension.sendAndWaitProtoMessageResponse
import galaxy.client.proto.Client
import galaxy.client.proto.Sloth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.google.protobuf.ByteString

class FeedbackRemoteManager(private val socketManager: WebSocketManager) {


    //800-1001: 反馈标签列表
    suspend fun getFeedbackLabelListRep(scope: CoroutineScope): ApiResponseState = withContext(scope.coroutineContext) {
        val result = socketManager.sendAndWaitProtoMessageResponse<Client.FeedbackLabelListResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.FEEDBACK_LABEL
        ) {
            Client.FeedbackLabelListReq.newBuilder().apply {
            }.build()
        }
        LogUtils.e("getFeedbackLabelListRep--------${result}")
        return@withContext if (result.error == null && result.data != null) {
            ApiResponseState.Succeeded(result.data!!.feedbackLabelList)
        } else {
            ApiResponseState.Failed(result.error)
        }
    }

    suspend fun feedbackContentAddReq(scope: CoroutineScope, code: ByteString, content: String): ApiResponseState = withContext(scope.coroutineContext){
        val result = socketManager.sendAndWaitProtoMessageResponse<Client.FeedbackContentAddResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.FEEDBACK_LABEL_ADD
        ) {
            Client.FeedbackContentAddReq.newBuilder().apply {
                this.setContent(content)
                this.setContentBytes(code)
            }.build()
        }
        return@withContext if (result.error == null) {
            ApiResponseState.Succeeded(true)
        } else {
            ApiResponseState.Failed(result.error)
        }
    }


}