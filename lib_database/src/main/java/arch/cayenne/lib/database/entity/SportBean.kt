package arch.cayenne.lib.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sport_bean", primaryKeys = ["sportId"])
data class SportBean(
    val sportId: Int,
    val sportName: String,
)
