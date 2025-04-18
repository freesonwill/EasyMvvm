package com.walisport.module.live.data.model

data class MatchLiveStreamBean @JvmOverloads constructor(
    val name: String = "",
    val urlSource: String = "",
    val streamType: String = "",
    val rtmpUrl: String = "",
    val m3U8Url: String = "",
    val flvUrl: String = "",
    val language: String = "",

    val sources: String = "",
    val thumb: String = "",
    val title: String = "",
    val subTitle: String = "",
    var isPlaying: Boolean = false
)
