package arch.cayenne.lib.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import arch.cayenne.lib.database.entity.MarketBean
import arch.cayenne.lib.database.entity.MarketSelectCrossRef
import arch.cayenne.lib.database.entity.MarketWithSelections
import arch.cayenne.lib.database.entity.MatchBean
import arch.cayenne.lib.database.entity.MatchMarketCrossRef
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.lib.database.entity.SelectionBean
import arch.cayenne.lib.database.entity.TournamentMatchRef
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Dao
abstract class MatchDao : BaseDao<MatchBean>() {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertTournamentMatchRef(crossRef: List<TournamentMatchRef>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertMatch(match: List<MatchBean>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertMarkets(markets: List<MarketBean>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertSelections(selections: List<SelectionBean>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertMatchMarketCrossRef(crossRef: List<MatchMarketCrossRef>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertMarketSelectionCrossRef(crossRef: List<MarketSelectCrossRef>)

    @RewriteQueriesToDropUnusedColumns
    @Transaction
    @Query("SELECT * " +
            "FROM MatchBean bean " +
            "INNER JOIN TournamentMatchRef ref ON ref.playType = :playType AND ref.tournamentId = :tournamentId AND ref.page = :page AND ref.startTime = :startTime " +
            "WHERE ref.matchId = bean.matchId")
    abstract suspend fun queryAllMatch(playType: Int, tournamentId: Int, page: Int, startTime: Long) : List<MatchBean>

    @Transaction
    @Query("SELECT * " +
            "FROM MatchBean bean " +
            "INNER JOIN TournamentMatchRef ref ON ref.playType = :playType AND ref.tournamentId = :tournamentId AND ref.page = :page AND ref.startTime = :startTime " +
            "WHERE ref.matchId = bean.matchId")
    abstract fun observeAllMatch(playType: Int, tournamentId: Int, page: Int, startTime: Long) : Flow<List<MatchBean>>

    @Transaction
    @Query("SELECT * FROM MatchBean WHERE matchId = :matchId")
    abstract suspend fun getMatchById(matchId: Long) : MatchBean

    @Transaction
    @Query("SELECT * " +
            "FROM MarketBean market " +
            "INNER JOIN  MatchMarketCrossRef ref ON ref.matchId = :matchId " +
            "WHERE market.marketId = ref.marketId ")
    abstract suspend fun geMarkets(matchId: Long): List<MarketBean>

    @Transaction
    @Query("SELECT * " +
            "FROM SelectionBean sel " +
            "INNER JOIN  MarketSelectCrossRef ref ON ref.matchId = :matchId AND ref.marketId = :marketId " +
            "WHERE sel.selectionId = ref.selectionId ")
    abstract suspend fun getSelections(matchId: Long, marketId: Long): List<SelectionBean>

    @Transaction
    @Query("SELECT * FROM SelectionBean WHERE selectionId = :selectionId")
    abstract suspend fun getSelectionById(selectionId: Long): SelectionBean

    @Query("DELETE FROM MatchBean" )
    abstract fun deleteMatchBean()

    @Query("DELETE FROM MarketBean" )
    abstract fun deleteMarketBean()

    @Query("DELETE FROM SelectionBean" )
    abstract fun deleteSelectionBean()

    @Query("DELETE FROM TournamentMatchRef" )
    abstract fun deleteTournamentMatchRef()

    @Query("DELETE FROM MatchMarketCrossRef" )
    abstract fun deleteMatchMarketCrossRef()

    @Query("DELETE FROM MarketSelectCrossRef" )
    abstract fun deleteMarketSelectCrossRef()



    @Transaction
    open suspend fun insertFullMatch(
        tournamentMatchRefs: List<TournamentMatchRef>,
        matches: List<MatchBean>,
        markets: List<MarketBean>,
        selections: List<SelectionBean>,
        marketCrossRef: List<MatchMarketCrossRef>,
        marketSelectCrossRefs: List<MarketSelectCrossRef>,
        ) {
        insertTournamentMatchRef(tournamentMatchRefs)
        insertMatch(matches)
        insertMarkets(markets)
        insertSelections(selections)
        insertMatchMarketCrossRef(marketCrossRef)
        insertMarketSelectionCrossRef(marketSelectCrossRefs)
    }

    @Transaction
    open suspend fun getFullMatch(playType: Int, tournamentId: Int, page: Int, startTime: Long): List<MatchWithMarkets> {
        return queryAllMatch(playType, tournamentId, page, startTime).map { matchBean ->
            val markets = geMarkets(matchBean.matchId).map { marketBean ->
                val selections = getSelections(matchBean.matchId, marketBean.marketId)
                MarketWithSelections(marketBean, selections)
            }
            MatchWithMarkets(matchBean, markets)
        }
    }


    open fun observeFullMatch(playType: Int, tournamentId: Int, page: Int, startTime: Long): Flow<List<MatchWithMarkets>> {
        return observeAllMatch(playType, tournamentId, page, startTime).map { matchBeanList ->
            matchBeanList.map { matchBean ->
                val markets = geMarkets(matchBean.matchId).map { marketBean ->
                    val selections = getSelections(matchBean.matchId, marketBean.marketId)
                    MarketWithSelections(marketBean, selections)
                }
                MatchWithMarkets(matchBean, markets)
            }
        }
    }

    @Transaction
    open suspend fun getOneMatchById(matchId: Long): MatchWithMarkets {
        return getMatchById(matchId).let { matchBean ->
            val markets = geMarkets(matchBean.matchId).map { marketBean ->
                val selections = getSelections(matchBean.matchId, marketBean.marketId)
                MarketWithSelections(marketBean, selections)
            }
            MatchWithMarkets(matchBean, markets)
        }
    }

    @Transaction
    open fun clearAllMatch() {
        deleteTournamentMatchRef()
        deleteMatchBean()
        deleteMarketBean()
        deleteMatchMarketCrossRef()
        deleteSelectionBean()
        deleteMarketSelectCrossRef()
    }
}
