package arch.cayenne.lib.database

import androidx.room.TypeConverter
import arch.cayenne.lib.database.entity.BetResultDetailBean
import arch.cayenne.lib.database.entity.BetResultStatusEnum
import arch.cayenne.lib.database.entity.SelectionLiteBean

class GameTypeConverter {

    @TypeConverter
    fun fromSelection(value: String): SelectionLiteBean {
        return value.split(",").let {
            SelectionLiteBean(
                marketName = it[0],
                id = it[1].toLong(),
                name = it[2],
                odds = it[3]
            )
        }
    }

    @TypeConverter
    fun toSelection(value: SelectionLiteBean): String {
        return "${value.marketName},${value.id},${value.name},${value.odds}"
    }

    @TypeConverter
    fun fromBetMoneyBean(value: String): List<BetResultDetailBean> {
        return value.split(",").map {
            val parts = it.split(":")
            BetResultDetailBean(
                orderId = parts[0],
                combo = parts[1].toInt(),
                sumOdds = parts[2].toInt(),
                count = parts[3].toInt(),
                inputMoney = parts[4].toLong(),
                statusEnum = BetResultStatusEnum.getStatusByCode(parts[5].toInt())
            )
        }
    }

    @TypeConverter
    fun toBetMoneyBean(value: List<BetResultDetailBean>): String {
        return value.joinToString(",") {
            "${it.orderId}:${it.combo}:${it.sumOdds}:${it.count}:${it.inputMoney}:${it.statusEnum.code}"
        }
    }

    @TypeConverter
    fun fromLongList(value: String): List<Long> {
        return value.split(",").map { it.toLong() }
    }

    @TypeConverter
    fun toLongList(value: List<Long>): String {
        return value.joinToString(",")
    }

}