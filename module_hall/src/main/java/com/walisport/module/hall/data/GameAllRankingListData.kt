package com.walisport.module.hall.data

data class GameAllRankingListData(
    val gameIconUrl: String ,
    val gameName: String ,
    val multiple: Int ,
    val symbol: String ,
    val icon: String ,
    val result: Float ,
    val virtual: Boolean ,//是否虚拟币
)
