package arch.cayenne.module.betslip.data.model

import galaxy.common.proto.Common

data class BetSlipOrderBean (
    val betId: String,                         // 下注id
    val betTime: Long,                         // 下注時間
    val settleTime: Long,                      // 結算時間
    val betAmount: String,                     // 下注總金額
    val returnAmount: String,                  // 正常結算派彩金額
    val selectionsList: List<OrderSelectionBean>,      // 選項
    val comboType: Int,                        // 串關類型：0-單關，1-串關，2-全串關
    val comboK: Int,
    val comboV: Int,
    val comboCount: Int,
    val odds: String,                          // 賠率
    val status: Int,                           // 訂單狀態：1投注確認中，2拒單，3取消訂單，4接單成功，5已結算
    val earlySupport: Boolean,                 // 是否支持提前結算
    val earlyBetAmount: String,                // 提前結算總本金
    val earlyReturnAmount: String,             // 提前結算派彩金額
    val earlySettleTimes: Int,                 // 已經提前結算的次數
    val resultStatus: Int,                     // 訂單結果：0-未結算，1-贏，2-和局，3-輸，4-輸一半，5-贏一半，6-退款，7-提前結算
    val earlySettlePrice: EarlySettlePriceBean // 提前結算報價
): BetSlipData()