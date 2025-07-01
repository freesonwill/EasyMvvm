package com.walisport.module.live.data.model

data class MatchEventBean @JvmOverloads constructor(
    var time: Int,
    var homeInit: Boolean = false,
    var awayInit: Boolean = false,
    var homeType: Int = 0,              //主队事件1类型
    var homePlayer: String = "",        //主队事件1球员
    var homeTwoType: Int = 0,           //主队事件2类型
    var homeTwoPlayer: String = "",     //主队事件2球员
    var awayType: Int = 0,              //客队事件1类型
    var awayPlayer: String = "",        //客队事件1球员
    var awayTwoType: Int = 0,           //客队事件2类型
    var awayTwoPlayer: String = "",     //客队事件2球员
)