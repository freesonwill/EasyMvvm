package arch.cayenne.lib.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

/***
 * @param matchId 赛事ID
 * @param selectionLiteBean 选择的盘口
 * @param reverseOdds 預約赔率
 * @param betType 0: 單注 1: 串關 2: 預約
 * @param status 下注狀態
 * @param leagueName 联赛名称 ex. 世界盃
 * @param matchName 赛事名称 ex. 中國 vs 日本
 * @param minAmount 最小下注金额
 * @param maxAmount 最大下注金额
 * @param isBetStop 是否停止下注
 * @param isPlaying 是否滾球
 */
@Entity(tableName = "BetBean")
data class BetBean(
    @PrimaryKey
    val matchId: Long, // 赛事ID
    var selectionLiteBean: SelectionLiteBean, // 选择的盘口
    var reverseOdds: Int? = null, // 預約赔率
    var betType: BetTypeEnum, // 0: 單注 1: 串關 2: 預約
    var status: BetStatusEnum = BetStatusEnum.PENDING_BET, // 下注狀態
    val leagueName: String, // 联赛名称 ex. 世界盃
    val matchName: String, // 赛事名称 ex. 中國 vs 日本
    var minAmount: Long, // 最小下注金额
    var maxAmount: Long, // 最大下注金额
    var isBetStop: Boolean = false, // 是否停止下注
    var isPlaying: Boolean = false, // 是否滾球
)

/***
 * @param marketName 盘口名称 ex. 讓分盤
 * @param id 盘口ID
 * @param name 盘口名称 ex. 中國 (+1.5)
 * @param odds 盘口赔率 ex. 1.9
 */
data class SelectionLiteBean(
    val marketName: String, // 盘口名称 ex. 讓分盤
    val id: Long, // 盘口ID
    var name: String, // 盘口名称 ex. 中國 (+1.5)
    var odds: String // 盘口赔率 ex. 1.9
)

enum class BetTypeEnum {
    SINGLE, COMBO, RESERVE
}

enum class BetStatusEnum {
    PENDING_BET, // 待下注
    FAIL, // 下注失敗
    BETTING, // 下注中
    COMPLETE // 下注完成
}

class BetTypeConverter {
    @TypeConverter
    fun fromBetTypeEnum(value: BetTypeEnum): Int = value.ordinal

    @TypeConverter
    fun toBetTypeEnum(value: Int): BetTypeEnum = BetTypeEnum.entries[value]

    @TypeConverter
    fun fromSelection(value: String): Selection {
        return value.split(",").let {
            Selection(
                marketName = it[0],
                id = it[1].toInt(),
                name = it[2],
                odds = it[3].toInt()
            )
        }
    }
    @TypeConverter
    fun toSelection(value: Selection): String {
        return "${value.marketName},${value.id},${value.name},${value.odds}"
    }
}