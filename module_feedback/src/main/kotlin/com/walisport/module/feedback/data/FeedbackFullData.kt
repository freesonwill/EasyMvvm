package com.walisport.module.live.data

import com.walisport.module.feedback.data.FeedbackLabel
import galaxy.common.proto.Common

data class FeedbackFullData(
    val feedbackList:  List<FeedbackLabel>,
    )

fun List<Common.FeedbackLabel>.toFeedbackLabelData(): FeedbackFullData {
    val feedbackList = mutableListOf<FeedbackLabel>()
    this.forEach{it->
        feedbackList.add(FeedbackLabel(code = it.code, name = it.name))
    }
    return FeedbackFullData(
        feedbackList = feedbackList
    )
}