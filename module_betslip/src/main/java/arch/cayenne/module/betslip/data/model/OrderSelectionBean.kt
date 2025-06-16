package arch.cayenne.module.betslip.data.model

import arch.cayenne.lib.database.entity.MatchBasicInfoBean
import galaxy.common.proto.Common

data class OrderSelectionBean(
    override val selectionId: Long,          // 投注项id
    val selectionName: String,      // 选项名称
    val odds: String,               // 下注赔率
    val marketName: String,         // 盘口名称
    val marketId: Long,             // 盘口id
    val specifier: String,          // 盘口说明符
    val betScore: String,           // 下注时比分
    val matchBasic: MatchBasicInfoBean, // 比赛基本信息
    val status: Int,                // 选项状态 0-未结算 1-赢 2-平 3-输 4-赢半 5-输半 6-取消
    val endScore: String,           // 结束时比分
    val inPlay: Boolean             // 是否滚球
): BetSlipSelectionData