package arch.cayenne.module.betslip.data.model

import arch.cayenne.lib.database.entity.MatchBasicInfoBean
import arch.cayenne.lib.database.entity.MatchLiveInfoBean

data class ReserveOrderSelectionBean(
    val selectionId: Long,          // 投注项 id
    val selectionName: String,      // 选项名称
    val odds: String,               // 预约下注赔率
    val marketName: String,         // 盘口名称
    val marketId: Long,             // 盘口 id
    val specifier: String,          // 盘口说明符
    val matchBasic: MatchBasicInfoBean,  // 比赛基本信息
    val liveInfo: MatchLiveInfoBean  // 比赛实时信息
): BetSlipSelectionData