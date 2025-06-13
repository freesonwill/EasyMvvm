package arch.cayenne.module.betslip.data.model

import galaxy.common.proto.Common

data class ReserveOrderSelectionBean(
    val selectionId: Long,          // 投注项 id
    val selectionName: String,      // 选项名称
    val odds: String,               // 预约下注赔率
    val marketName: String,         // 盘口名称
    val marketId: Long,             // 盘口 id
    val specifier: String,          // 盘口说明符
    val matchBasic: Common.MatchBasicInfo  // 比赛基本信息
): BetSlipSelectionData()

fun Common.ReserveOrderSelection.toReserveOrderSelectionBean(): ReserveOrderSelectionBean {
    return ReserveOrderSelectionBean(
        selectionId = selectionId,
        selectionName = selectionName,
        odds = odds,
        marketName = marketName,
        marketId = marketId,
        specifier = specifier,
        matchBasic = matchBasic
    )
}