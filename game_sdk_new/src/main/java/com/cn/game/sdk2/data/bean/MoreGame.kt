package com.cn.game.sdk2.data.bean

/**
 * 游戏大厅数据
 */
data class MoreGame(
    val idp:Int,
    val gameType:Int,
    var name:String,
    val weight:Int,
    val direction:Int,
    val icon:String
){
    var online:Int = 0

    //是否是本地游戏
    val isLocal get() = !icon.startsWith("http")
}



