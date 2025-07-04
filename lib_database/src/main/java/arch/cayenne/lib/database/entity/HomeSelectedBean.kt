package arch.cayenne.lib.database.entity

import androidx.room.Entity

@Entity(primaryKeys = ["playType","sportId","tournamentId"])
data class HomeSelectedDao(
    val playType: Int,
    val sportId: Int,
    val tournamentId: Int,
    val date: Long
)