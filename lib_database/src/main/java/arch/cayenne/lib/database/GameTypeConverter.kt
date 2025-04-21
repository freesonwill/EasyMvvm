package arch.cayenne.lib.database

import androidx.room.TypeConverter
import arch.cayenne.lib.database.entity.BetTypeEnum
import arch.cayenne.lib.database.entity.Selection

class GameTypeConverter {


    @TypeConverter
    fun fromBetTypeEnum(value: BetTypeEnum): Int = value.ordinal

    @TypeConverter
    fun toBetTypeEnum(value: Int): BetTypeEnum = BetTypeEnum.entries[value]

    @TypeConverter
    fun fromSelection(value: String): Selection {
        return value.split(",").let {
            Selection(
                marketName = it[0],
                id = it[1].toLong(),
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