package com.cn.game.sdk2.websocket.bean

/**
 * 下注：服务器返回时通知ui
 */
data class BettingResponsesBean(
    val isSuccess: Boolean,
    var money: Int
)