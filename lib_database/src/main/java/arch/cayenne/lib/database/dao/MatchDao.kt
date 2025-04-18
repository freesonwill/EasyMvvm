package arch.cayenne.lib.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import arch.cayenne.lib.database.entity.MarketBean
import arch.cayenne.lib.database.entity.MarketDetailBean
import arch.cayenne.lib.database.entity.MarketDetailWithSelections
import arch.cayenne.lib.database.entity.MarketWithMarketDetails
import arch.cayenne.lib.database.entity.MatchBean
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.lib.database.entity.SelectionBean

@Dao
abstract class MatchDao : BaseDao<MatchBean>() {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertMatch(match: List<MatchBean>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertMarkets(markets: List<MarketBean>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertMarketDetails(details: List<MarketDetailBean>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertSelections(selections: List<SelectionBean>)

    // 事務性插入 （先後插入主表和子表）
    @Transaction
    open suspend fun insertFullMatch(
        matches: List<MatchBean>,
        markets: List<MarketBean>,
        marketDetails: List<MarketDetailBean>,
        selections: List<SelectionBean>
    ) {
        insertMatch(matches)
        insertMarkets(markets)
        insertMarketDetails(marketDetails)
        insertSelections(selections)
    }


    // 取得 Match + 他的 Markets
    @Transaction
    @Query("SELECT * FROM MatchBean WHERE matchId = :matchId")
    abstract suspend fun getMatchWithMarkets(matchId: Int): MatchWithMarkets

    // 取得 Market + 它的 Details
    @Transaction
    @Query("SELECT * FROM MarketBean WHERE marketId IN (:marketIds)")
    abstract suspend fun getMarketsWithDetails(marketIds: List<Int>): List<MarketWithMarketDetails>

    // 取得 Detail + 它的 Selections
    @Transaction
    @Query("SELECT * FROM MarketDetailBean WHERE detailId IN (:detailIds)")
    abstract suspend fun getDetailsWithSelections(detailIds: List<Int>): List<MarketDetailWithSelections>

    // 查整筆 Match + 所有 Market -> Detail -> Selection
    @Transaction
    @Query("SELECT * FROM MatchBean WHERE matchId = :id")
    abstract suspend fun getFullMatchById(id: Long): MatchWithMarkets?
}
