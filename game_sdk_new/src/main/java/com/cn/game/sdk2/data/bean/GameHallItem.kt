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

    //是否是本地游戏
    val iconIsLocal get() = !icon.startsWith("http")
}
