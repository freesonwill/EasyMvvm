package arch.cayenne.lib.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class HomeSelectedBean(
    @PrimaryKey val playType: Int,
    val sportId: Int,
    val tournamentId: Int,
    val date: Long
)