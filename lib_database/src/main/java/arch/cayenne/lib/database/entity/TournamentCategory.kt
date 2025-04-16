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

data class TournamentDataModel(
    val id: Int,
    val name: String,
    val simpleName: String,
    val icon: String,
    val weight: Int,
)