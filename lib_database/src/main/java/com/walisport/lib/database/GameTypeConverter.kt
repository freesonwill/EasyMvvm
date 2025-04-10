package com.walisport.lib.database

import androidx.room.TypeConverter
import com.walisport.lib.database.enum.BetStatusEnum
import com.walisport.lib.database.enum.BetTypeEnum

class GameTypeConverter {

    @TypeConverter
    fun fromBetTypeEnum(value: BetTypeEnum): Int {
        return value.ordinal
    }

    @TypeConverter
    fun toBetTypeEnum(value: Int): BetTypeEnum {
        return BetTypeEnum.entries[value]
    }

    @TypeConverter
    fun fromBetStatusEnum(value: BetStatusEnum): Int {
        return value.ordinal
    }

    @TypeConverter
    fun toBetStatusEnum(value: Int): BetStatusEnum {
        return BetStatusEnum.entries[value]
    }
}