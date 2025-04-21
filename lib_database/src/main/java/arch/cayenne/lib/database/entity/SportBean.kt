package arch.cayenne.lib.database.entity

import androidx.room.Entity

@Entity(tableName = "sport_bean", primaryKeys = ["sportId"])
data class SportBean(
    val sportId: Int,
    val sportName: String,
)

@Entity(primaryKeys = ["playType","sportId"])
data class PlayTypeSportCrossRef(
    val playType: Int,
    val sportId: Int,
    val matchCount: Int,
    val sportOrder: Int
)
