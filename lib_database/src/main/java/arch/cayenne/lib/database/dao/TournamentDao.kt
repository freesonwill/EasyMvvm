package arch.cayenne.lib.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import arch.cayenne.lib.database.entity.ChampionTournamentDataModel
import arch.cayenne.lib.database.entity.SportTournamentCrossRef
import arch.cayenne.lib.database.entity.TournamentBean
import arch.cayenne.lib.database.entity.TournamentDataModel
import kotlinx.coroutines.flow.Flow

@Dao
abstract class TournamentDao: BaseDao<TournamentBean>() {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertSportTournamentCrossRefs(refs: List<SportTournamentCrossRef>)

    @Query("SELECT * FROM SportTournamentCrossRef WHERE playType = :playTypeId AND sportId = :sportId ANd tournamentId = :tournamentId ")
    abstract suspend fun getSportTournamentCrossRef(playTypeId: Int, sportId: Int, tournamentId: Int): SportTournamentCrossRef?

    @Query("UPDATE SportTournamentCrossRef SET coordinateY = :coordinate WHERE playType = :playTypeId AND sportId = :sportId AND tournamentId = :tournamentId ")
    abstract suspend fun updateRefCoordinate(playTypeId: Int, sportId: Int, tournamentId: Int, coordinate: Int)

    @Query("SELECT bean.id as id, " +
            "ref.sportId as sportId, " +
            "ref.playType as playTypeId, " +
            "bean.name as name, " +
            "bean.simpleName as simpleName, " +
            "bean.icon as icon, " +
            "ref.weight as weight, " +
            "ref.hot as hot " +
            "FROM TournamentBean bean " +
            "INNER JOIN SportTournamentCrossRef ref ON ref.tournamentId = bean.id " +
            "order by weight desc, `index` asc "
    )
    abstract fun observeTournamentWithLimit(): Flow<List<TournamentDataModel>>

    @Query("SELECT bean.id as id, " +
            "ref.sportId as sportId, " +
            "ref.playType as playTypeId, " +
            "bean.name as name, " +
            "bean.simpleName as simpleName, " +
            "bean.icon as icon, " +
            "ref.weight as weight, " +
            "ref.hot as hot " +
            "FROM TournamentBean bean " +
            "INNER JOIN SportTournamentCrossRef ref ON ref.tournamentId = bean.id " +
            "WHERE ref.playType =:playTypeId and ref.sportId =:sportId " +
            "order by weight desc, `index` asc"
    )
    abstract suspend fun queryTournaments(playTypeId: Int, sportId: Int): List<TournamentDataModel>

    @Query("SELECT bean.id as id, " +
            "ref.sportId as sportId, " +
            "ref.playType as playTypeId, " +
            "bean.name as name, " +
            "bean.simpleName as simpleName, " +
            "bean.icon as icon, " +
            "ref.weight as weight, " +
            "ref.hot as hot, " +
            "ref.matchId as championMatchId " +
            "FROM TournamentBean bean " +
            "INNER JOIN SportTournamentCrossRef ref ON ref.tournamentId = bean.id " +
            "WHERE ref.playType =:playTypeId and ref.sportId =:sportId AND ref.matchId != null " +
            "order by weight desc, `index` asc"
    )
    abstract suspend fun queryChampionTournaments(playTypeId: Int, sportId: Int): List<ChampionTournamentDataModel>

    @Query("SELECT bean.id as id, " +
            "ref.sportId as sportId, " +
            "ref.playType as playTypeId, " +
            "bean.name as name, " +
            "bean.simpleName as simpleName, " +
            "bean.icon as icon, " +
            "ref.weight as weight, " +
            "ref.hot as hot " +
            "FROM TournamentBean bean " +
            "INNER JOIN SportTournamentCrossRef ref ON ref.tournamentId = bean.id " +
            "WHERE ref.playType =:playTypeId and ref.sportId =:sportId and tournamentId = :tournamentId " +
            "order by weight desc, `index` asc"
    )
    abstract fun queryTournament(playTypeId: Int, sportId: Int, tournamentId: Int): TournamentDataModel?

    @Query("DELETE FROM SportTournamentCrossRef " +
            "WHERE sportId = :sportId AND playType = :playType AND tournamentId NOT IN (:ids)")
    abstract suspend fun deleteMissing(sportId: Int, playType: Int, ids: List<Int>)

    @Query("DELETE FROM SportTournamentCrossRef")
    abstract suspend fun clearAllSportTournamentCrossRef()

    @Query("DELETE FROM TournamentBean")
    abstract suspend fun clearAllTournaments()
}