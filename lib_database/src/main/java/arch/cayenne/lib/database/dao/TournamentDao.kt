package arch.cayenne.lib.database.dao

import android.os.FileObserver.DELETE
import androidx.room.Dao
import androidx.room.Query
import arch.cayenne.lib.database.entity.TournamentBean
import arch.cayenne.lib.database.entity.TournamentDataModel

@Dao
abstract class TournamentDao: BaseDao<TournamentBean>() {
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    abstract fun insertTournamentRef(data: List<SportTournamentCrossRef>): List<Long>
//    @Query("SELECT tb.id as id , tb.name as name, tb.simpleName as simpleName, tb.icon as icon, weight as weight " +
//            "FROM SportTournamentCrossRef " +
//            "INNER JOIN TournamentBean tb ON tb.id = tournamentId " +
//            "WHERE  sportId = :sportId and playType = :playType order by weight desc limit :limit"
//    )
//    abstract fun queryTournamentWithLimit(playType: Int, sportId: Int, limit: Int): List<TournamentDataModel>
//
//    @Query("SELECT tb.id as id , tb.name as name, tb.simpleName as simpleName, tb.icon as icon, weight as weight " +
//            "FROM SportTournamentCrossRef " +
//            "INNER JOIN TournamentBean tb ON tb.id = tournamentId " +
//            "WHERE  sportId = :sportId and playType = :playType order by weight desc"
//    )
//    abstract fun queryTournament(playType: Int, sportId: Int): List<TournamentDataModel>

    @Query("SELECT bean.id as id, bean.sportId as sportId, bean.name as name, bean.simpleName as simpleName, bean.icon as icon, bean.weight as weight " +
            "FROM TournamentBean bean " +
            "order by weight desc limit :limit"
    )
    abstract fun queryTournamentWithLimit(limit: Int): List<TournamentDataModel>

    @Query("SELECT bean.id as id, bean.sportId as sportId, bean.name as name, bean.simpleName as simpleName, bean.icon as icon, bean.weight as weight " +
            "FROM TournamentBean bean " +
            "order by weight desc"
    )
    abstract fun queryTournament(): List<TournamentDataModel>

    @Query("DELETE FROM TournamentBean")
    abstract fun clearTournaments()
}