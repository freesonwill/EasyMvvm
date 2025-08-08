package arch.cayenne.lib.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 *
 * @date: 2025/8/8 17:14
 * @description:
 */
@Entity
data class RechargeRecordBean(
    @PrimaryKey val id: Int, //充值记录id
    val amount: String, //充值金额或数量
)