package com.walisport.module.live.data.model

data class MatchEventBean @JvmOverloads constructor(
    val id: Int = 0,
    val minutes: Int,
    val homeType:Int,         //主队事件1类型
    val homePlayer: String,   //主队事件1球员
    val homeTypeTwo: Int,     //主队事件2类型
    val homePlayerTwo: String,//主队事件2类型
    val awayType:Int,         //客队事件1类型
    val awayPlayer: String,   //客队事件1球员
    val awayTypeTwo: Int,     //客队事件2类型
    val awayPlayerTwo: String,//客队事件2球员
)