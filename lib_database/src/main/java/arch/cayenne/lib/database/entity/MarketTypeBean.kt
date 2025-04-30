package arch.cayenne.lib.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "MarketTypeBean")
data class MarketTypeBean(
    val code: String = "", // 盘口id
    val name: String = "", // 盘口名称
    @PrimaryKey
    val marketId: Long = 0, // 选项id
    val marketName: String = "", // 选项名称
    val isSelect: Boolean = false// 是否选中
)