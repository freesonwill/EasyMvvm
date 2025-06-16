package arch.cayenne.lib.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

interface BetSlipData
interface BetSlipSelectionData

@Entity(tableName = "BetSlipOrderBean")
data class BetSlipOrderBean (
    @PrimaryKey
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
    @Embedded
    val earlySettlePrice: EarlySettlePriceBean, // 提前結算報價
    var betSlipType: Int
): BetSlipData

data class OrderSelectionBean(
    val selectionId: Long,          // 投注项id
    val selectionName: String,      // 选项名称
    val odds: String,               // 下注赔率
    val marketName: String,         // 盘口名称
    val marketId: Long,             // 盘口id
    val specifier: String,          // 盘口说明符
    val betScore: String,           // 下注时比分
    @Embedded
    val matchBasic: MatchBasicInfoBean, // 比赛基本信息
    val status: Int,                // 选项状态 0-未结算 1-赢 2-平 3-输 4-赢半 5-输半 6-取消
    val endScore: String,           // 结束时比分
    val inPlay: Boolean             // 是否滚球
): BetSlipSelectionData

data class EarlySettlePriceBean(
    val price: String,          // 提前結算 1 元的報價（如 0.92）
    val settleTotal: Int,       // 最大有效提前結算次數
    val settleMin: String,      // 單次最小結算本金
    val settleStatus: Int       // 狀態：1-投注確認中, 2-拒單, ..., 102-提前結算進行中
)

