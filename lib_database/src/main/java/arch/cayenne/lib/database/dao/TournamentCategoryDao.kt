package arch.cayenne.lib.database.dao

import androidx.room.Dao
import androidx.room.Query
import arch.cayenne.lib.database.entity.TournamentCategory
import arch.cayenne.lib.database.entity.TournamentDataModel

@Dao
abstract class TournamentCategoryDao: BaseDao<TournamentCategory>() {
    @Query("SELECT tb.id as id , tb.name as name, tb.simpleName as simpleName, tb.icon as icon, weight as weight " +
            "FROM tournament_category " +
            "INNER JOIN tournament_bean tb ON tb.id = tournamentId " +
            "WHERE  sportId = :sportId and playType = :playType order by weight desc limit :limit"
    )
    abstract fun queryTournamentWithLimit(playType: Int, sportId: Int, limit: Int): List<TournamentDataModel>

    @Query("SELECT tb.id as id , tb.name as name, tb.simpleName as simpleName, tb.icon as icon, weight as weight " +
            "FROM tournament_category " +
            "INNER JOIN tournament_bean tb ON tb.id = tournamentId " +
            "WHERE  sportId = :sportId and playType = :playType order by weight desc"
    )
    abstract fun queryTournament(playType: Int, sportId: Int): List<TournamentDataModel>
}