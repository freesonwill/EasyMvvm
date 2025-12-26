package com.walisport.module.hall.data

import com.walisport.module.business.common.data.GameContentData


data class GameAllContentData(
    val name: String, //游戏名称
    val category: Int, //游戏分类
    var gameList: List<GameContentData> //游戏
)




