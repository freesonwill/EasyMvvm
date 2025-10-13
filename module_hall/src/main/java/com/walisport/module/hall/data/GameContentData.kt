package com.walisport.module.hall.data

data class GameContentData(
    val cover: Int,    //TODO 要換成String
    val hotOrCold: HotColdType,
    val percent: Float,
    val onlineCount: Int,
)

enum class HotColdType {
    HOT, COLD, NONE
}
