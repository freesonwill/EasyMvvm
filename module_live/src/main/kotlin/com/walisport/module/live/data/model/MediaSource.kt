package com.walisport.module.live.data.model

import arch.cayenne.lib.database.entity.VideoSourceBean

/**
 *
 * @date: 2025/10/29 16:21
 * @description:
 */
data class MediaSource(
    val mediaSourceType: MediaSourceType,
    val animationUrl: String?,
    val videoSourceBean: VideoSourceBean?,
    var isPlaying: Boolean
)

enum class MediaSourceType {
    ANIMATION, VIDEO
}

