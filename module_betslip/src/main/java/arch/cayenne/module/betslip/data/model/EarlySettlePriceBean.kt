package arch.cayenne.module.betslip.data.model

import galaxy.common.proto.Common

data class EarlySettlePriceBean(
    val betId: String,          // 訂單 ID
    val price: String,          // 提前結算 1 元的報價（如 0.92）
    val settleTotal: Int,       // 最大有效提前結算次數
    val settleMin: String,      // 單次最小結算本金
    val settleStatus: Int       // 狀態：1-投注確認中, 2-拒單, ..., 102-提前結算進行中
)

fun Common.EarlySettlePrice.toEarlySettlePriceBean(): EarlySettlePriceBean {
    return EarlySettlePriceBean(
        betId = betId,
        price = price,
        settleTotal = settleTotal,
        settleMin = settleMin,
        settleStatus = settleStatus
    )
}