package com.walisport.module.live.data.model

/**
 * 比赛趋势数据
 */

data class MatchTrendData @JvmOverloads constructor(
    val incidents: List<Incidents>,
    val data: List<Int>
)

data class Incidents @JvmOverloads constructor(
    val time: String,  //时间(分钟)
    val position: Int, //事件发生方，1-主队、2-客队
    val type: Int
)