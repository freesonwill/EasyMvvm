package arch.cayenne.lib.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TournamentBean(
    @PrimaryKey val id: Int,
    val name: String,
    val simpleName: String,
    val icon: String,
    val hot: Boolean,
    val weight: Int,
)

//@Entity(primaryKeys = ["tournamentId", "sportId", "playType"])
//data class SportTournamentCrossRef(
//    val tournamentId: Int,
//    val sportId: Int,
//    val playType: Int,
//    val hot: Boolean,
//    val weight: Int,
//)

data class TournamentDataModel(
    val id: Int,
    val name: String,
    val simpleName: String,
    val icon: String,
    val weight: Int,
) {
    companion object {
        fun createAllItem(): TournamentDataModel {
            return TournamentDataModel(
                id = 0,
                name = "ALL",
                simpleName = "ALL",
                icon = "",
                weight = Int.MAX_VALUE
            )
        }
    }
}
