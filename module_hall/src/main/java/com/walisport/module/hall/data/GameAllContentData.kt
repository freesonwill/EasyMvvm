package com.walisport.module.hall.data


data class GameAllContentData(
    val name: String, //游戏名称
    val category: Int, //游戏分类
    var gameList: List<GameContentData> //游戏
)




