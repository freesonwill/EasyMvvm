package arch.cayenne.lib.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "MarketMenuBean")
data class MarketMenuBean(
    @PrimaryKey
    val orderNumber:Int =  0,
    val marketId: Long = 0, // 盘口id
    val marketName: String = "", // 盘口名称
    var isSelect: Boolean = false,// 是否选中
    val code: String = "", // 盘口id
)