package arch.cayenne.lib.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 *
 * @date: 2026/1/3 21:32
 * @description:
 */
@Entity
data class DailyBetMatchDataBean(
    @PrimaryKey val id: Int = 1,
    val ccy: String, // 货币缩写(ISO4217)
    val betScore: Long, // 投注奖金金额
    val remainingTime: Long,// 活动剩余时长/s
    val myBetScore: Long, // 我的投注金额，未投注则为0
    val myRank: Int? // 我的排名，未上榜则为null
)