package com.walisport.module.live.data.model

/**
 * 比赛统计数据
 */

data class MatchLiveData @JvmOverloads constructor(
    val score: ScoreData,
    val stats: List<Stat>,                 // 比赛统计字段说明，可能不存在
    val tlive: List<TLive>,               // 文字直播字段说明，可能不存在
    val incidents: List<Incident>,        // 比赛事件字段说明，可能不存在
    val teamStats: Map<String, List<Int>>,//比赛球队半全场统计数据
    val id: Int                           // 比赛Id
)

data class ScoreData @JvmOverloads constructor(
    val match_id: Long,
    val state: Int,
    val home_scores: List<Int>,
    val away_scores: List<Int>,
    val start_time: Long,
    val remark: String
)

data class Stat @JvmOverloads constructor(
    val away: Int,  // 客队值
    val state: Int, //类型
    val home: Int   // 主队值
)

data class TLive @JvmOverloads constructor(
    val match_id: Long,
    val state: Int,
    val home_scores: List<Int>,
    val away_scores: List<Int>,
    val start_time: Long,
    val remark: String
)

data class Incident @JvmOverloads constructor(
    var position: Int, // 事件发生方，0-中立、1-主队、2-客队
    var time: Int,// 事件时间(分钟)
    var type: Int, // 类型，详见状态码->技术统计
    var second: Int, // 事件时间(秒数)
    var in_player_id: Long, // 换上球员Id，可能不存在
    var in_player_name_zh: String = "", // 换上球员名称(中文简体)，可能不存在;
    var in_player_name_zht: String = "", // 换上球员名称(中文繁体)，可能不存在
    var in_player_name_en: String = "", // 换上球员名称(英文)，可能不存在
    var out_player_id: Long = 0, // 换下球员Id，可能不存在
    var out_player_name_zh: String = "", // 换下球员名称(中文简体)，可能不存在
    var out_player_name_zht: String = "", // 换下球员名称(中文繁体)，可能不存在
    var out_player_name_en: String = "", // 换下球员名称(英文)，可能不存在
    var reason_type: Int = 0, // 红黄牌、换人事件原因，详见状态码->事件原因（红黄牌、换人事件存在）
    var player_id: Long = 0, // 事件相关球员id，可能不存在
    var player_name_zh: String = "", // 事件相关球员名称(中文简体)，可能不存在
    var player_name_zht: String = "", // 事件相关球员名称(中文繁体)，可能不存在
    var player_name_en: String = "", // 事件相关球员名称(英文)，可能不存在
    var var_reason: Int = 0, // VAR原因（VAR事件存在）1-进球判定,2-进球未判定,3-点球判定,4-点球未判定,5-红牌判定,6-出牌处罚判定,7-错认身份,0-其他
    var var_result: Int = 0, // VAR结果（VAR事件存在）1-进球有效,2-进球无效,3-点球有效,4-点球取消,5-红牌有效,6-红牌取消,7-出牌处罚核实,8-出牌处罚更改,9-维持原判,10-判罚更改,0-未知
    var home_score: Int = 0, // 主队比分（进球、未进球 事件存在）
    var away_score: Int = 0, // 客队比分（进球、未进球 事件存在）
    var assist1_id: Long = 0, // 助攻球员一Id，可能不存在
    var assist1_name_zh: String = "",// 助攻球员一名称(中文简体)，可能不存在
    var assist1_name_zht: String = "",// 助攻球员一名称(中文繁体)，可能不存在
    var assist1_name_en: String = "", // 助攻球员一名称(英文)，可能不存在
    var assist2_id: Long = 26, // 助攻球员二Id，可能不存在
    var assist2_name_zh: String = "",// 助攻球员二名称(中文简体)，可能不存在
    var assist2_name_zht: String = "", // 助攻球员二名称(中文繁体)，可能不存在
    var assist2_name_en: String = "" // 助攻球员二名称(英文)，可能不存在
)
