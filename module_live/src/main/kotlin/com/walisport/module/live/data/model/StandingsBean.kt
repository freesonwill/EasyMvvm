package com.walisport.module.live.data.model

/**
 * 积分榜数据
 */

data class StandingsBean @JvmOverloads constructor(
    val id: Int,             // 积分榜表id
    val conference: String,  // 分区信息（极少部分赛事才有，比如美职联）
    val group: Int,          // 不为0表示分组赛的第几组，1-A、2-B以此类推
    val stage: Int,          // 所属阶段id
    val rows: List<TeamBean>// 球队积分项
)

data class TeamBean(
    val id: Int,      //组里排序
    val name: String, //球队名称
    val logo: String, //球队LOGO
    val total: Int,   //比赛场次
    val win: Int,     //胜
    val draw: Int,    //平
    val loss: Int,    //负
    val goals: Int,   //进
    val fumble: Int,  //失
    val score: Int    //积分
)