package arch.cayenne.lib.database.entity

import androidx.room.Entity


@Entity(primaryKeys = ["id"])
data class GameBean(
    val id: Int,
    val clickFlag: Int
)
