package com.walisport.module.hall.data.constants

import com.walisport.module.hall.data.AvatarVo
import com.walisport.module.hall.data.GameAllRankingListData

/**
 *
 * @date: 2025/12/18 21:58
 * @description:
 */
data class BigVo(
    val id: Long , // 游戏ID
    val name: String , // 玩家名称
    val avatar: AvatarVo , // 图片信息
    val ccy: String , // 货币缩写(ISO4217)
    val multiple: Int , // 投注倍率
    val bonus: Int // 输赢金额
)

//BigVo转换为GameAllRankingListData
fun BigVo.toGameAllRankingListData(): GameAllRankingListData {
    return GameAllRankingListData(
        gameIconUrl = avatar.url ,
        gameName = name ,
        multiple = multiple.toFloat() ,
        countryIcon = when (ccy) {
            "USD" -> arch.cayenne.lib.common.R.drawable.ic_usdt
            "CNY" -> arch.cayenne.lib.common.R.drawable.ic_cny
            else -> arch.cayenne.lib.common.R.drawable.ic_usdt
        } ,
        symbol = when (ccy) {
            "USD" -> "$"
            "CNY" -> "¥"
            else -> "$"
        } ,
        result = bonus.toFloat()
    )
}