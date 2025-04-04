package com.walisport.module.live.data.model

/**
 * 联赛日程信息
 */

data class LeagueMatchBean @JvmOverloads constructor(
    val id: Int = 0,
    val timeStamp: Long,
    val homeTeamLogo: String,
    val awayTeamLogo: String,
    val homeTeamName: String,
    val awayTeamName: String,
) {

}