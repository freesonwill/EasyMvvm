package arch.cayenne.lib.database

import androidx.room.TypeConverter
import arch.cayenne.lib.database.entity.BetTypeEnum
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

}