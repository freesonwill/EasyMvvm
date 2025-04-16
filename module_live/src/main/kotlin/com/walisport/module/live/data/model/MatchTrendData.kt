package com.walisport.module.live.data.model

/**
 * 比赛趋势数据
 */

data class MatchTrendData @JvmOverloads constructor(
    val data: List<Data>,
    val count: Int,// 半场数
    val incidents: List<Incidents>,// 事件列表
    val per: Int,// 半场时长
)

data class Data @JvmOverloads constructor(
    val values: List<Int>
)

data class Incidents @JvmOverloads constructor(
    val time: String,  //时间(分钟)
    val position: Int, //事件发生方，1-主队、2-客队
    val type: Int
)