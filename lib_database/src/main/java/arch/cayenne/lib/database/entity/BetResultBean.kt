package arch.cayenne.lib.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.random.Random

@Entity
data class BetResultBean(
    @PrimaryKey
    val id: Long = Random.nextLong(),
    val selectionIds: List<Long>,
    val detail: List<BetResultDetailBean>
)

enum class BetResultStatusEnum(val code: Int) {
    SUCCESS_CREATE(0),
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

data class BetResultDetailBean(
    val orderId: String,
    val combo: Int = 1, // 串關次數
    val sumOdds: Int, // 串關後賠率加總
    val count: Int = 1, // 場次組合數量
    val inputMoney: Long,
    val statusEnum: BetResultStatusEnum
)