package com.walisport.module.hall.data

data class GameAllContentData(
    val id: Long,//游戏id
    val name: String, //游戏名称
    var gameList: List<GameContentData> //游戏
)




