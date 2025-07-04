package arch.cayenne.lib.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import arch.cayenne.lib.database.entity.SportTournamentCrossRef
import arch.cayenne.lib.database.entity.TournamentBean
import arch.cayenne.lib.database.entity.TournamentDataModel
import kotlinx.coroutines.flow.Flow

@Dao
abstract class TournamentDao: BaseDao<TournamentBean>() {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertSportTournamentCrossRefs(refs: List<SportTournamentCrossRef>)

    @Query("SELECT bean.id as id, " +
            "ref.sportId as sportId, " +
            "bean.name as name, " +
            "bean.simpleName as simpleName, " +
            "bean.icon as icon, " +
            "ref.weight as weight, " +
            "ref.hot as hot " +
            "FROM TournamentBean bean " +
            "INNER JOIN SportTournamentCrossRef ref ON ref.tournamentId = bean.id " +
            "WHERE playType =:playType and sportId =:sportId " +
            "order by weight desc, `index` asc limit :limit"
    )
    abstract fun observeTournamentWithLimit(playType: Int, sportId: Int, limit: Int): Flow<List<TournamentDataModel>>

    @Query("SELECT bean.id as id, " +
            "ref.sportId as sportId, " +
            "bean.name as name, " +
            "bean.simpleName as simpleName, " +
            "bean.icon as icon, " +
            "ref.weight as weight, " +
            "ref.hot as hot " +
            "FROM TournamentBean bean " +
            "INNER JOIN SportTournamentCrossRef ref ON ref.tournamentId = bean.id " +
            "order by weight desc, `index` asc "
    )
    abstract fun queryTournament(): List<TournamentDataModel>

    @Query("DELETE FROM SportTournamentCrossRef " +
            "WHERE sportId = :sportId AND playType = :playType AND tournamentId NOT IN (:ids)")
    abstract suspend fun deleteMissing(sportId: Int, playType: Int, ids: List<Int>)

}