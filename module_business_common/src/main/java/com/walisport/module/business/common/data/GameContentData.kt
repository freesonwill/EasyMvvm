package com.walisport.module.business.common.data

data class Avatar(
    val url: String,
    val thumbhash: String,
    val css: String
)

data class GameContentData(
    val gameID: Long,//游戏id
    val name: String, //游戏名称
    val avatar: Avatar, //游戏icon
    val online: Int, //游戏在线人数
    val reward: Double, //奖率
    val hasMore: Boolean, //是否有返奖
    val hotOrCold: HotColdType,
)




