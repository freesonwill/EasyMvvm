package arch.cayenne.lib.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "BetBean")
data class BetBean(
    @PrimaryKey
    val betId: Long = System.currentTimeMillis(),
    var betType: BetTypeEnum = BetTypeEnum.SINGLE, // 0: 單注 1: 串關 2: 預約
    var status: BetStatusEnum = BetStatusEnum.PENDING, // 下注狀態
)

@Entity(
    primaryKeys = ["betId", "combo"],
)
data class BetDetailBean(
    val betId: Long,
    var orderId: String = "",
    val combo: Int = 1, // 串關次數
    val sumOdds: Int, // 串關後賠率加總
    val count: Int = 1, // 場次組合數量
    val inputMoney: Long,
    var status: BetResultStatusEnum? = null
)

/**
 * 首頁盤口監聽投注項用
 */
data class BetSelectionLiteBean(
    val matchId: Long, // 赛事ID
    val selectionId: Long, // 盘口ID
)


@Entity(
    primaryKeys = ["betId", "matchId"],
)
data class BetSelectionBean(
    val betId: Long,
    val matchId: Long,
    val marketName: String, // 盘口名称 ex. 讓分盤
    val selectionId: Long, // 盘口ID
    val name: String, // 盘口名称 ex. 中國 (+1.5)
    var odds: Int, // 盘口赔率 ex. 1.9
    val leagueName: String, // 联赛名称 ex. 世界盃
    val matchName: String, // 赛事名称 ex. 中國 vs 日本
    var isActive: Boolean, // 是否停止下注
    var isPlaying: Boolean, // 是否滾球
    var isParlay: Boolean
)

enum class BetTypeEnum {
    SINGLE, COMBO, RESERVE
}

enum class BetStatusEnum {
    PENDING, // 待下注
    BETTING, // 下注中
    COMPLETE // 下注完成, 完成並非成功!!
}

enum class BetResultStatusEnum(val code: Int) {
    CREATE(0),
    CONFIRMING(1),
    REJECT(2),
    CANCEL(3),
    SUCCESS_BET(4),
    SETTLED(5);

    companion object {
        fun getStatusByCode(code: Int): BetResultStatusEnum {
            return entries.first { it.code == code }
        }
    }

}