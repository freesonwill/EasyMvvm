package com.walisport.lib.database

import androidx.room.TypeConverter
import com.walisport.lib.database.entity.BetTypeEnum

class GameTypeConverter {


    @TypeConverter
    fun fromBetTypeEnum(value: BetTypeEnum): Int = value.ordinal

    @TypeConverter
    fun toBetTypeEnum(value: Int): BetTypeEnum = BetTypeEnum.entries[value]

}