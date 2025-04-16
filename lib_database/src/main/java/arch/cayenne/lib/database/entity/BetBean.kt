package arch.cayenne.lib.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "BetBean")
data class BetBean(
    @PrimaryKey
    val matchId: Int,
    var selection: Selection,
    var reverseOdds: Int? = null,
    var betType: BetTypeEnum, // 0: 單注 1: 串關 2: 預約
    var status: BetStatusEnum = BetStatusEnum.PENDING_BET, // 下注狀態
    val leagueName: String,
    val matchName: String,
    var isBetStop: Boolean = false,
    val isPlaying: Boolean = false,
)

data class Selection(
    val marketName: String,
    val id: Int,
    var name: String,
    var odds: Int
)

enum class BetTypeEnum {
    SINGLE, COMBO, RESERVE
}

enum class BetStatusEnum {
    PENDING_BET, // 待下注
    CLOSE, // 盤口關閉
    FAIL, // 下注失敗
    BETTING, // 下注中
    COMPLETE // 下注完成
}