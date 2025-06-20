package arch.cayenne.lib.database.dao

import androidx.room.Dao
import androidx.room.Query
import arch.cayenne.lib.database.entity.TournamentBean
import arch.cayenne.lib.database.entity.TournamentDataModel
import kotlinx.coroutines.flow.Flow

@Dao
abstract class TournamentDao: BaseDao<TournamentBean>() {

    @Query("SELECT bean.id as id, " +
            "bean.sportId as sportId, " +
            "bean.name as name, " +
            "bean.simpleName as simpleName, " +
            "bean.icon as icon, " +
            "bean.weight as weight, " +
            "bean.hot as hot " +
            "FROM TournamentBean bean WHERE playType =:playType and sportId =:sportId " +
            "order by weight desc limit :limit"
    )
    abstract fun observeTournamentWithLimit(playType: Int, sportId: Int, limit: Int): Flow<List<TournamentDataModel>>

    @Query("SELECT bean.id as id, " +
            "bean.sportId as sportId, " +
            "bean.name as name, " +
            "bean.simpleName as simpleName, " +
            "bean.icon as icon, " +
            "bean.weight as weight, " +
            "bean.hot as hot " +
            "FROM TournamentBean bean " +
            "order by weight desc"
    )
    abstract fun queryTournament(): List<TournamentDataModel>

    @Query("SELECT bean.id as id, " +
            "bean.sportId as sportId, " +
            "bean.name as name, " +
            "bean.simpleName as simpleName, " +
            "bean.icon as icon, " +
            "bean.weight as weight, " +
            "bean.hot as hot " +
            "FROM TournamentBean bean " +
            "WHERE bean.id = :id"
    )
    abstract fun getTournamentById(id: Int): TournamentDataModel?

    @Query("DELETE FROM TournamentBean WHERE id NOT IN (:keepIds)")
    abstract suspend fun deleteMissing(keepIds: List<Int>)
}