package com.walisport.module.live.data.model

/**
 * 积分榜数据结构
 */

data class CompetitionBean @JvmOverloads constructor(
    val tables: List<TableBean>
)

data class TableBean @JvmOverloads constructor(
    val id: Long,            //积分榜表id
    val conference: String,  //分区信息（极少部分赛事才有，比如美职联）
    val group: Int,          //不为0表示分组赛的第几组，1-A、2-B以此类推
    val stage_id: Long,      //所属阶段id
    val rows: List<TeamStats>//球队积分项
)

data class TeamStats @JvmOverloads constructor(
    val team_id: Long,       //球队id
    val points: Int,         //积分
    val total: Int,          //比赛场次
    val won: Int,            //胜的场次
    val draw: Int,           //平的场次
    val loss: Int,           //负的场次
    val goals: Int,          //进球
    val goals_against: Int,  //失球
    val team_name: String,   //球队名称
    val team_logo: String,   //球队logo
)