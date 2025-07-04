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
    primaryKeys = ["betId", "serialValue"],
)
data class BetDetailBean(
    val betId: Long,
    var orderId: String = "",
    val serialValue: Int = 0, // 多少串一關，0為全串關
    val comboK: Int = 1, // 3串2的3
    val comboV: Int = 1, // 3串2的2
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
    val marketId: Long, // 盘口ID
    val marketName: String, // 盘口名称 ex. 讓分盤
    val selectionId: Long, // 盘口ID
    val name: String, // 盘口名称 ex. 中國 (+1.5)
    var odds: Int, // 盘口赔率 ex. 1.9
    val leagueName: String, // 联赛名称 ex. 世界盃
    val matchName: String, // 赛事名称 ex. 中國 vs 日本
    var isActive: Boolean, // 是否停止下注
    var isPlaying: Boolean, // 是否滾球
    var isParlay: Boolean,
    val provider: Int,
    var oddsStatus: OddsStatusEnum? = null
) {
    fun updateOdds(newOdds: Int) {
        val lastOdds = odds
        odds = newOdds
        oddsStatus = if (lastOdds == newOdds) {
            OddsStatusEnum.SAME
        } else if (lastOdds > newOdds) {
            OddsStatusEnum.DOWN
        } else {
            OddsStatusEnum.UP
        }
    }

}

enum class BetTypeEnum {
    SINGLE, COMBO, RESERVE
}

enum class BetStatusEnum {
    PENDING, // 待下注
    BETTING, // 下注中
    COMPLETE // 下注完成, 完成並非成功!!
}

enum class AddSelectionStatus {
    SINGLE,
    COMBO,
    DISABLE_COMBO_FOR_PARLAY, // isParlay = false
    DISABLE_COMBO_FOR_PROVIDER, // 不同供應商
    UPDATE,
    REMOVE,
    MAX_LIMIT,
    FAIL
}

enum class OddsStatusEnum {
    UP,
    DOWN,
    SAME
}

enum class BetResultStatusEnum(val code: Int) {
    CREATE(0),
    CONFIRMING(1),
    REJECT(2),
    CANCEL(3),
    SUCCESS_BET(4),
    SETTLED(5),
    FAIL(100);

    companion object {
        fun getStatusByCode(code: Int): BetResultStatusEnum {
            return entries.find { it.code == code } ?: FAIL
        }
    }

}

interface BetResultLiteBean {
    val isSuccessful: Boolean
}

data class SingleBetResultBean(
    val matchName: String,
    val selectionName: String,
    override val isSuccessful: Boolean
): BetResultLiteBean

data class ComboBetResultBean(
    val matchName: List<String>,
    val comboK: Int,
    val comboV: Int,
    override val isSuccessful: Boolean
): BetResultLiteBean