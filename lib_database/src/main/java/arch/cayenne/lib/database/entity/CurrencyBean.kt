package arch.cayenne.lib.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CurrencyBean (
    @PrimaryKey val id: Int,
    val virtual: Boolean,
    val rate: Double,
    val unit: String,
    val name: String
)