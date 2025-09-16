package com.walisport.module.live.data.model


data class LiveMarketListBean(
    val marketId: Long = 0, // 注区id
    val marketName: String = "", // 注区名称
    val code: String = "", // 盘口id
    var list:List<LiveMarketSelectionBean>
)

data class LiveMarketSelectionBean(
    val code: String = "", // 盘口id
    val selectionId: Long,
    val name: String,
    val shortName: String,
    val odds: String,
    val active: Boolean, //true - 可以投注  false - 不可投注
    val parlay: Boolean,
    val marketId: Long,
    val marketName: String,
    val style: Int,//0-默认 1-一列 2-两列 3-三列 4-波胆
    val oddsStatus: Int,
    var isSelect: Boolean = false,// 是否选中
)