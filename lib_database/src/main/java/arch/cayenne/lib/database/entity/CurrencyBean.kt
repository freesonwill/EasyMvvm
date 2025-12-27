package arch.cayenne.lib.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CurrencyBean (
    @PrimaryKey val id: Int,
    val name: String,
    val ccy: String,
    val crypto: Boolean,
    val scale: Long,
    val unit: String,
    val icon: String,
    val rate: Double
)