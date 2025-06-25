package arch.cayenne.lib.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TournamentBean(
    @PrimaryKey val id: Int,
    val playType: Int,
    val sportId: Int,
    val name: String,
    val simpleName: String,
    val icon: String,
    val hot: Boolean,
    val weight: Int,
    val index: Int,
)

//@Entity(primaryKeys = ["tournamentId", "sportId", "playType"])
//data class SportTournamentCrossRef(
//    val tournamentId: Int,
//    val sportId: Int,
//    val playType: Int,
//    val hot: Boolean,
//    val weight: Int,
//)

abstract class BaseTournamentData {
    abstract val id: Int
    abstract val sportId: Int
    abstract val name: String
    abstract val simpleName: String
    abstract val icon: String
    abstract val hot: Boolean
    abstract val weight: Int
}

data class TournamentDataModel(
    override val id: Int,
    override val sportId: Int,
    override val name: String,
    override val simpleName: String,
    override val icon: String,
    override val hot: Boolean,
    override val weight: Int,
) : BaseTournamentData() {
    companion object {
        fun createAllItem(sportId: Int): TournamentDataModel {
            return TournamentDataModel(
                id = 0,
                sportId = sportId,
                name = "ALL",
                simpleName = "ALL",
                icon = "",
                hot = false,
                weight = Int.MAX_VALUE
            )
        }
    }
}

data class ChampionTournamentDataModel(
    val championMatchId: Long,
    override val id: Int,
    override val sportId: Int,
    override val name: String,
    override val simpleName: String,
    override val icon: String,
    override val hot: Boolean,
    override val weight: Int,
): BaseTournamentData()
