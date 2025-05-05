package com.walisport.module.live.data.model

/**
 * 比赛统计数据
 */

data class MatchLiveData @JvmOverloads constructor(
    val id: Long,                        // 比赛ID
    val team: List<MatchHalfTeamStats>,  // 比赛球队半全场统计数据
    val stats: List<Stat>,               // 比赛统计字段说明，可能不存在
)

data class Stat @JvmOverloads constructor(
    val type: Int,                        // 类型
    val home: Int,                        // 主队值
    val away: Int,                        // 客队值
)

data class MatchHalfTeamStats @JvmOverloads constructor(
    val type: Int,                        // 类型
    val homeNum: Int,                     // 主队值
    val awayNum: Int                      // 客队值
)
