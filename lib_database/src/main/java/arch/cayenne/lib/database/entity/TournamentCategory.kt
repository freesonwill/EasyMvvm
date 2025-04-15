package arch.cayenne.lib.database.entity

import androidx.room.Entity

@Entity(tableName = "tournament_category", primaryKeys = ["tournamentId", "sportId", "playType"])
data class TournamentCategory(
    val tournamentId: Int,
    val sportId: Int,
    val playType: Int,
    val hot: Boolean,
    val weight: Int,
)