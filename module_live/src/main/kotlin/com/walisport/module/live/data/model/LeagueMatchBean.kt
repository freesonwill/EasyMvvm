package com.walisport.module.live.data.model

/**
 * 联赛日程信息
 */

data class LeagueMatchBean @JvmOverloads constructor(
    val match: List<MatchBean>,      //比赛列表
    val tournamentName: String,      //联赛名称
    val tournamentShortName: String, //联赛缩写
    val logo: String,                //联赛图标
    val color: String                 //联赛日程页渐变背景色
)

data class MatchBean @JvmOverloads constructor(
    val matchId: Long = 0,
    val sportId: Int = 0,
    val isWeekHead: Boolean = false,  //true就显示日期归档Header，false则显示正常联赛Item
    val weekDay: String = "",
    val homeLogo: String,
    val homeName: String,
    val awayLogo: String,
    val awayName: String,
    val startTime: Long
)