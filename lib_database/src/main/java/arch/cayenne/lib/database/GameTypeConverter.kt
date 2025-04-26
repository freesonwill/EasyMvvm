package arch.cayenne.lib.database

import androidx.room.TypeConverter
import arch.cayenne.lib.database.entity.BetSelectionBean

class GameTypeConverter {


    @TypeConverter
    fun fromLongList(value: String): List<Long> {
        return value.split(",").map { it.toLong() }
    }

    @TypeConverter
    fun toLongList(value: List<Long>): String {
        return value.joinToString(",")
    }

}