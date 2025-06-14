package com.walisport.module.live.data.model

/**
 * 比赛统计数据
 */

data class MatchLineupDetail @JvmOverloads constructor(
    val confirmed: Int,           // 正式阵容，1-是、0-否
    val homeFormation: String = "",       // 主队阵型
    val awayFormation: String = "",      // 客队阵型
    val homeColor: String = "",         // 主队球衣颜色
    val awayColor: String = "",           // 客队球衣颜色
    val home: List<Player>,        // 主队阵型球员列表
    val away: List<Player>,      // 客队阵型球员列表
    val homeId: Int,           // 主队Id
    val homeLogo: String = "",          // 主队logo
    val awayId: Int,          // 客队Id
    val awayLogo: String = ""          // 客队logo
)

data class Player @JvmOverloads constructor(
    val id: Int,             // 球员id
    val teamId: Int,         // 球队id
    val first: Int,          // 是否首发，1-是、0-否
    val captain: Int,        // 是否队长，1-是、0-否
    val name: String = "",           // 球员名称
    val logo: String = "",          // 球员logo
    val nationalLogo: String = "", // 球员logo(国家队)
    val shirtNumber: Int,    // 球衣号
    val position: String = "",       // 球员位置，F前锋、M中场、D后卫、G守门员
    val x: Int,              // 阵容x坐标，总共100
    val y: Int,              // 阵容y坐标，总共100
    val rating: String = "",       // 评分，10为满分
    val incidents: List<PlayerIncident> // 球员事件列表
)

data class PlayerIncident @JvmOverloads constructor(
    val type: Int,             // 事件类型（参考技术类型）
    val time: String = "",               // 事件发生时间（含加时时间，如 'A+B'）
    val belong: Int,            // 发生方，0-中立、1-主队、2-客队
    val homeScore: Int,         // 主队比分
    val awayScore: Int,         // 客队比分
    val player: PlayerInfo,     // 球员信息
    val assist1: PlayerInfo,     // 助攻球员1
    val assist2: PlayerInfo,     // 助攻球员2
    val inPlayer: PlayerInfo,     // 换上球员
    val outPlayer: PlayerInfo,     // 换下球员
    val reasonType: Int,        // 红黄牌、换人事件原因
)

data class PlayerInfo @JvmOverloads constructor(
    val id: Int,          // 球员id
    val name: String = ""          // 中文名称
)


