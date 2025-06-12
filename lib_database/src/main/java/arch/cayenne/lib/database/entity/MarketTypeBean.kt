package arch.cayenne.lib.database.entity

import androidx.databinding.adapters.Converters
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "MarketTypeBean")
data class MarketTypeBean(
    @PrimaryKey
    val code: String = "", // 盘口id
    val name: String = "", // 盘口名称
)