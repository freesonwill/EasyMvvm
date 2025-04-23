package arch.cayenne.lib.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import arch.cayenne.lib.base.utils.LogUtilsExt.loge
import arch.cayenne.lib.database.entity.MarketBean
import arch.cayenne.lib.database.entity.MarketSelectCrossRef
import arch.cayenne.lib.database.entity.MarketWithSelections
import arch.cayenne.lib.database.entity.MatchBean
import arch.cayenne.lib.database.entity.MatchMarketCrossRef
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.lib.database.entity.SelectionBean

@Dao
abstract class MatchDao : BaseDao<MatchBean>() {

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

    @Transaction
    @Query("SELECT * FROM MatchBean ")
    abstract suspend fun getAllMatch() : List<MatchBean>

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

    @Transaction
    open suspend fun insertFullMatch(
        matches: List<MatchBean>,
        markets: List<MarketBean>,
        selections: List<SelectionBean>,
        marketCrossRef: List<MatchMarketCrossRef>,
        marketSelectCrossRefs: List<MarketSelectCrossRef>,
        ) {
        insertMatch(matches)
        insertMarkets(markets)
        insertSelections(selections)
        insertMatchMarketCrossRef(marketCrossRef)
        insertMarketSelectionCrossRef(marketSelectCrossRefs)
    }

    @Transaction
    open suspend fun getFullMatch(): List<MatchWithMarkets> {
        return getAllMatch().map { matchBean ->
            val markets = geMarkets(matchBean.matchId).map { marketBean ->
                val selections = getSelections(matchBean.matchId, marketBean.marketId)
                MarketWithSelections(marketBean, selections)
            }
            MatchWithMarkets(matchBean, markets)
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
}
