package arch.cayenne.lib.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "LiveVideoBean")
data class LiveVideoBean(
    @PrimaryKey
    val id: Int,
    val url: String,
)
