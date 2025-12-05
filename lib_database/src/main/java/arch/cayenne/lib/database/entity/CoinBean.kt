package arch.cayenne.lib.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "CoinBean")
data class CoinBean(
    @PrimaryKey val id: Int,
    val virtual: Boolean,
    var isSelect: Boolean,
    val name: String,
    var icon: String,
    var unit: String
)