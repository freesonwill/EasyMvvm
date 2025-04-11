package arch.cayenne.lib.database

import androidx.room.TypeConverter
import arch.cayenne.lib.database.entity.BetTypeEnum

class GameTypeConverter {


    @TypeConverter
    fun fromBetTypeEnum(value: BetTypeEnum): Int = value.ordinal

    @TypeConverter
    fun toBetTypeEnum(value: Int): BetTypeEnum = BetTypeEnum.entries[value]

}