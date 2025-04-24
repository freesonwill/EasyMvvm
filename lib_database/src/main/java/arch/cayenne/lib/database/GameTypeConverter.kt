package arch.cayenne.lib.database

import androidx.room.TypeConverter
import arch.cayenne.lib.database.entity.BetMoneyBean
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
    fun fromBetMoneyBean(value: String): List<BetMoneyBean> {
        return value.split(",").map {
            val parts = it.split(":")
            BetMoneyBean(
                combo = parts[0].toInt(),
                sumOdds = parts[1].toInt(),
                count = parts[2].toInt(),
                inputMoney = parts[3].toLong(),
                statusEnum = BetResultStatusEnum.valueOf(parts[4])
            )
        }
    }

    @TypeConverter
    fun toBetMoneyBean(value: List<BetMoneyBean>): String {
        return value.joinToString(",") {
            "${it.combo}:${it.sumOdds}:${it.count}:${it.inputMoney}:${it.statusEnum.name}"
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