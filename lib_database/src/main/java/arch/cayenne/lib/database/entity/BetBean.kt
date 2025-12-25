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
    var sumOdds: Int, // 串關後賠率加總
    var odds: Int,
    val count: Int = 1, // 場次組合數量
    var inputMoney: Long,
    val currency: String,
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
    val sportId: Int,
    val matchId: Long,
    val marketId: Long, // 盘口ID
    var marketName: String, // 盘口名称 ex. 讓分盤
    val selectionId: Long, // 盘口ID
    var name: String, // 盘口名称 ex. 中國 (+1.5)
    var odds: Int, // 盘口赔率 ex. 1.9
    val initialOdds: Int, // 初始赔率
    var leagueName: String, // 联赛名称 ex. 世界盃
    var matchName: String, // 赛事名称 ex. 中國 vs 日本
    var isActive: Boolean, // 是否停止下注
    var isPlaying: Boolean, // 是否滾球
    var isParlay: Boolean, //是否串关
    val provider: Int, //提供商ID
    val createTime: Long = System.currentTimeMillis(),
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
    COMPLETE, // 下注完成, 完成並非成功!!
    DONE
}

enum class OddsStatusEnum {
    UP, //赔率上升
    DOWN, //赔率下降
    SAME //赔率不变
}

enum class BetResultStatusEnum(val code: Int) {
    CREATE(0),
    CONFIRMING(1), //确认中
    REJECT(2),
    CANCEL(3),
    SUCCESS_BET(4),
    SETTLED(5), //结算
    FAIL(100);  //失败

    companion object {
        fun getStatusByCode(code: Int): BetResultStatusEnum {
            return entries.find { it.code == code } ?: FAIL
        }
    }

}

interface BetResultLiteBean {
    val currency: String
    val money: Long
    val isSuccessful: Boolean
}

data class SingleBetResultBean(
    val sportId: Int,
    val matchName: String,
    val selectionName: String,
    override val isSuccessful: Boolean,
    override val money: Long,
    override val currency: String
): BetResultLiteBean

data class ComboBetResultBean(
    val sportIds: List<Int>,
    val matchName: List<String>,
    val comboK: Int,
    val comboV: Int,
    override val isSuccessful: Boolean,
    override val money: Long,
    override val currency: String
): BetResultLiteBean