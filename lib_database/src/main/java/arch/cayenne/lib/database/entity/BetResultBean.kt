package arch.cayenne.lib.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BetResultBean(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val selectionIds: List<Long>
)

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


@Entity(
    tableName = "BetResultDetailBean",
    primaryKeys = ["betResultId", "combo"],
)
data class BetResultDetailBean(
    val betResultId: Long,
    var orderId: String = "",
    val combo: Int = 1, // 串關次數
    val sumOdds: Int, // 串關後賠率加總
    val count: Int = 1, // 場次組合數量
    val inputMoney: Long,
    var status: BetResultStatusEnum = BetResultStatusEnum.CONFIRMING,
)