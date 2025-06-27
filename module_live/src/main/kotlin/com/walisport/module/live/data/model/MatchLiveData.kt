package com.walisport.module.live.data.model

/**
 * 比赛统计数据
 */

data class MatchLiveData @JvmOverloads constructor(
    val id: Long,                        // 比赛ID
    val team: List<MatchHalfTeamStats>,  // 比赛球队半全场统计数据
    val stats: List<Stat>,               // 比赛统计字段说明，可能不存在
    val matchTrendData: MatchTrendData,  // 比赛趋势
    val incidents: List<Incident>        // 比赛事件
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

data class Incident @JvmOverloads constructor(
    val position: Int,               // 事件发生方，0-中立、1-主队、2-客队
    val time: Int,                   // 事件时间(分钟)
    val type: Int,                   // 类型，详见状态码->技术统计
    val in_player_name_zh: String,   // 换上球员名称(中文简体)，可能不存在
    val in_player_name_zht: String,  // 换上球员名称(中文繁体)，可能不存在
    val in_player_name_en: String,   // 换上球员名称(英文)，可能不存在
    val out_player_name_zh: String,  // 换下球员名称(中文简体)，可能不存在
    val out_player_name_zht: String, // 换下球员名称(中文繁体)，可能不存在
    val out_player_name_en: String,  // 换下球员名称(英文)，可能不存在
    val player_name_zh: String,      // 事件相关球员名称(中文简体)，可能不存在
    val player_name_zht: String,     // 事件相关球员名称(中文繁体)，可能不存在
    val player_name_en: String,      // 事件相关球员名称(英文)，可能不存在
    val assist1_name_zh: String,     // 助攻球员一名称(中文简体)，可能不存在
    val assist1_name_zht: String,    // 助攻球员一名称(中文繁体)，可能不存在
    val assist1_name_en: String,     // 助攻球员一名称(英文)，可能不存在
    val assist2_name_zh: String,     // 助攻球员二名称(中文简体)，可能不存在
    val assist2_name_zht: String,    // 助攻球员二名称(中文繁体)，可能不存在
    val assist2_name_en: String,     // 助攻球员二名称(英文)，可能不存在
)


