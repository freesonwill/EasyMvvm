package com.walisport.module.live.data.model

/**
 * 联赛日程信息
 */

data class LeagueMatchBean @JvmOverloads constructor(
    val id: Int = 0,
    val match: MatchBean,
    val startColor: String,//线性渐变开始色
    val endColor: String     //线性渐变结束色
)

data class MatchBean @JvmOverloads constructor(
    val id: Int = 0,
    val isWeek: Boolean,
    val weekDay: String,
    val timeStamp: Long,
    val homeTeamLogo: String,
    val awayTeamLogo: String,
    val homeTeamName: String,
    val awayTeamName: String,
)