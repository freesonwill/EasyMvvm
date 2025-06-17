package arch.cayenne.lib.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "BetSlipReserveBean")
data class BetSlipReserveBean(
    @PrimaryKey
    val reserveId: String,          // 下注 ID
    val reserveTime: Long,          // 預約時間（毫秒時間戳）
    val betAmount: String,          // 下注金額（字串格式）
    @Embedded
    val selection: ReserveOrderSelectionBean, // 預約選項（需要定義對應的資料類型）
    val betStatus: Int                 // 狀態：0-預約中, 1-成功, 2-失敗, 3-取消
) : BetSlipData

data class ReserveOrderSelectionBean(
    val selectionId: Long,          // 投注项 id
    val selectionName: String,      // 选项名称
    var odds: String,               // 预约下注赔率
    val marketName: String,         // 盘口名称
    val marketId: Long,             // 盘口 id
    val specifier: String,          // 盘口说明符
    @Embedded
    val matchBasic: MatchBasicInfoBean,  // 比赛基本信息
    @Embedded
    val liveInfo: MatchLiveInfoBean  // 比赛实时信息
): BetSlipSelectionData