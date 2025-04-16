package arch.cayenne.lib.database.entity

import androidx.room.Entity

@Entity(tableName = "sport_category", primaryKeys = ["playType","sportId"])
data class SportCategory(
    val playType: Int,
    val sportId: Int,
    val matchCount: Int,
    val sportOrder: Int
)
