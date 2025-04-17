package arch.cayenne.lib.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

/**
 * @author: zhangsan
 * @date: 2025/4/14 10:35
 * @description:
 */
@Entity(tableName = "ExampleBean")
data class ExampleBean(
    @PrimaryKey
    val id: Int,
    var colorType: ColorTypeEnum
)

enum class ColorTypeEnum {
    RED, GREEN, YELLOW
}

class ColorTypeConverter {
    @TypeConverter
    fun fromTypeEnum(value: ColorTypeEnum): Int = value.ordinal

    @TypeConverter
    fun toTypeEnum(value: Int): ColorTypeEnum = ColorTypeEnum.entries[value]
}

