package com.cn.game.sdk2.websocket.bean

data class RoundInfoBean(
    var roundId: String,
    var performs: List<Int>,
    var sum: Int,
    var isBig: Boolean,
    var isDouble: Boolean
)
