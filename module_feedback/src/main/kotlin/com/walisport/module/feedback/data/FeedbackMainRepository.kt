package com.walisport.module.feedback.data

import android.annotation.SuppressLint
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.data.repository.BaseRepository
import com.walisport.module.feedback.FeedbackRemoteManager
import com.walisport.module.live.data.toFeedbackLabelData
import galaxy.common.proto.Common
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FeedbackMainRepository(
    private val remoteManager: FeedbackRemoteManager
) : BaseRepository() {
    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    //获取反馈标签
    @SuppressLint("SuspiciousIndentation")
    suspend fun getFeedbackLabelList():ApiResponseState = withContext(scope.coroutineContext) {
        val state = remoteManager.getFeedbackLabelListRep(scope)
            if (state is ApiResponseState.Succeeded<*>) {
                val feedbackLabel:List<Common.FeedbackLabel>? = state.data as List<Common.FeedbackLabel>?
                if (feedbackLabel != null) {
                    return@withContext( ApiResponseState.Succeeded(feedbackLabel.toFeedbackLabelData().feedbackList))
                }
            }
            return@withContext(state)
    }


}

