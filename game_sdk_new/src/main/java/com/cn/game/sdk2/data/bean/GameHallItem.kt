package com.cn.game.sdk2.data.bean

data class GameHallItem(
    val idp:Int,
    val gameType:Int,
    val weight:Int,
    val direction:Int,
    val icon:String,
    val name:String,
) {
    var online:Int = 0
    /**
     * 是否是本地游戏 0-App 1-网络 2-本地
     */
    val iconType:Int get() = when {
        icon.startsWith("http") ->  1
        icon.startsWith("/") -> 2
        else -> 0
    }

    override fun toString(): String {
        return "name:$name"
    }
}
