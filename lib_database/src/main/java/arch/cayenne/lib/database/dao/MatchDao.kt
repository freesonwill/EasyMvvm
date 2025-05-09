package arch.cayenne.lib.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import arch.cayenne.lib.database.entity.MarketBean
import arch.cayenne.lib.database.entity.MarketBeanLite
import arch.cayenne.lib.database.entity.MarketSelectCrossRef
import arch.cayenne.lib.database.entity.MarketWithSelections
import arch.cayenne.lib.database.entity.MatchBean
import arch.cayenne.lib.database.entity.MatchBeanLite
import arch.cayenne.lib.database.entity.MatchMarketCrossRef
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.lib.database.entity.SelectionBean
import arch.cayenne.lib.database.entity.SelectionBeanLite
import arch.cayenne.lib.database.entity.TournamentMatchRef
import kotlinx.coroutines.flow.Flow

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

    @Query("DELETE FROM MarketSelectCrossRef WHERE matchId IN (:matchId) AND marketId IN (:marketId) ")
    abstract suspend fun deleteMarketSelectionCrossRef(matchId: List<Long>, marketId:List<Long>)

    @Query("SELECT *" +
            "FROM TournamentMatchRef " +
            "WHERE playType = :playType AND tournamentId = :tournamentId  ORDER BY `order`")
    abstract fun observeMatchChange(playType: Int, tournamentId: Int): Flow<List<TournamentMatchRef>>

    @RewriteQueriesToDropUnusedColumns
    @Transaction
    @Query("SELECT * " +
            "FROM MatchBean bean " +
            "INNER JOIN TournamentMatchRef ref ON ref.playType = :playType AND ref.tournamentId = :tournamentId AND ref.page = :page AND ref.startTime = :startTime " +
            "WHERE ref.matchId = bean.matchId ORDER BY ref.`order`")
    abstract suspend fun queryAllMatch(playType: Int, tournamentId: Int, page: Int, startTime: Long) : List<MatchBean>

    @Transaction
    @Query("delete " +
            "FROM TournamentMatchRef " +
            "WHERE playType = :playType AND tournamentId = :tournamentId AND startTime = :startTime")
    abstract fun deleteCurrentTournamentMatchRef(playType: Int, tournamentId: Int, startTime: Long)

    @Transaction
    @Query("SELECT * " +
            "FROM MatchBean bean " +
            "INNER JOIN TournamentMatchRef ref ON ref.playType = :playType AND ref.tournamentId = :tournamentId AND ref.page = :page AND ref.startTime = :startTime " +
            "WHERE ref.matchId = bean.matchId")
    abstract fun observeAllMatch(playType: Int, tournamentId: Int, page: Int, startTime: Long) : Flow<List<MatchBean>>

    @Transaction
    @Query("SELECT * FROM MatchBean WHERE matchId = :matchId")
    abstract suspend fun getMatchById(matchId: Long) : MatchBean

    @Query("SELECT * FROM MatchBean WHERE matchId = :matchId")
    abstract fun observeMatchById(matchId: Long): Flow<MatchBean?>

    @Transaction
    @Query("SELECT * FROM MatchBean WHERE matchId IN (:matchIds)")
    abstract suspend fun getMatchByIds(matchIds: List<Long>) : List<MatchBean>

    @Transaction
    @Query("SELECT market.marketId as marketId, market.marketName as marketName, market.status as status, ref.selectionCount as defaultSelectionCount " +
            "FROM MarketBean market " +
            "INNER JOIN  MatchMarketCrossRef ref ON ref.matchId = :matchId " +
            "WHERE market.marketId = ref.marketId ")
    abstract suspend fun geMarkets(matchId: Long): List<MarketBeanLite>

    @Transaction
    @Query("SELECT sel.selectionId as selectionId, " +
                "ref.matchId as matchId,  " +
                "sel.detail_active as detailActive, " +
                "sel.name as name, " +
                "sel.shortName as shortName, " +
                "sel.odds as odds, " +
                "sel.active as active, " +
                "sel.parlay as parlay, " +
                "0 as isSelected," +
                "0 as trend " +
            "FROM SelectionBean sel " +
            "INNER JOIN  MarketSelectCrossRef ref ON ref.matchId = :matchId AND ref.marketId = :marketId " +
            "WHERE sel.selectionId = ref.selectionId ")
    abstract suspend fun getSelectionLites(matchId: Long, marketId: Long): List<SelectionBeanLite>

    @Transaction
    @Query("SELECT * FROM SelectionBean WHERE selectionId = :selectionId")
    abstract suspend fun getSelectionById(selectionId: Long): SelectionBean

    @Transaction
    @Query("SELECT * FROM SelectionBean WHERE selectionId IN (:selectionIds)")
    abstract suspend fun getSelectionsByIds(selectionIds: List<Long>): List<SelectionBean>

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

    //收到notify時，match不是全收到，沒收到的那些也是不會變動的，所以只更新有收到的參數
    @Query("UPDATE MatchBean " +
            "SET basic_status = :status, " +
                "basic_betStop = :betStop, " +
                "basic_startTime = :startTime," +
                "live_clock = :clock, " +
                "live_rollClock = :rollClock, " +
                "live_period = :period, " +
                "live_score = :score, " +
                "live_liveVideo = :liveVideo, " +
                "live_charRoom = :charRoom, " +
                "live_viewerCount = :viewerCount, " +
                "live_clockModified = :clockModified " +
            "WHERE matchId = :matchId")
    abstract fun updateNotifyMatch(
        matchId: Long,
        status: Int,
        betStop: Boolean,
        startTime: Long,
        clock: Int,
        rollClock: Boolean,
        period: String,
        score: String,
        liveVideo: Boolean,
        charRoom: Boolean,
        viewerCount: Int,
        clockModified: Long
    )

    @Query(
        "UPDATE MatchBean SET collect = :collect WHERE matchId = :matchId")
    abstract fun updateOnlyMatchCollect(matchId: Long, collect: Boolean)

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
        //盤口的selection有可能在推播時整個變更（例如兩個選項+0.5/-0.5 -> +1/-1），所以刪除之前的cross ref，把之前盤口和selection連結斷開再連接，避免query取得之前的盤口
        deleteMarketSelectionCrossRef(marketCrossRef.map { it.matchId }, marketCrossRef.map { it.marketId })
        insertMarketSelectionCrossRef(marketSelectCrossRefs)
    }

    @Transaction
    open suspend fun updateFullMatch(
        matchLites: List<MatchBeanLite>,
        markets: List<MarketBean>,
        selections: List<SelectionBean>,
        marketCrossRef: List<MatchMarketCrossRef>,
        marketSelectCrossRefs: List<MarketSelectCrossRef>,
    ): List<MatchWithMarkets> {
        matchLites.forEach { bean ->
            updateNotifyMatch(
                matchId = bean.matchId,
                status = bean.status,
                betStop = bean.betStop,
                startTime = bean.startTime,
                clock = bean.liveInfo.clock,
                rollClock = bean.liveInfo.rollClock,
                period = bean.liveInfo.period,
                score = bean.liveInfo.score,
                liveVideo = bean.liveInfo.liveVideo,
                charRoom = bean.liveInfo.charRoom,
                viewerCount = bean.liveInfo.viewerCount,
                clockModified = bean.liveInfo.clockModified,
            )
        }
        val oldOdds =
            getSelectionsByIds(selections.map { it.selectionId }).associate { it.selectionId to it.odds }
        insertMarkets(markets)
        insertSelections(selections)
        insertMatchMarketCrossRef(marketCrossRef)
        //盤口的selection有可能在推播時整個變更（例如兩個選項+0.5/-0.5 -> +1/-1），所以刪除之前的cross ref，把之前盤口和selection連結斷開再連接，避免query取得之前的盤口
        deleteMarketSelectionCrossRef(marketCrossRef.map { it.matchId }, marketCrossRef.map { it.marketId })
        insertMarketSelectionCrossRef(marketSelectCrossRefs)

        return getOneMatchByIds(matchLites.map { it.matchId }).onEach {
            //加入賠率趨勢
            it.markets.forEach { markets ->
                markets.selections.forEach { selection ->
                    if (oldOdds.containsKey(selection.selectionId)) {
                        selection.trend = selection.odds - oldOdds[selection.selectionId]!!
                    }
                }
            }
        }
    }

    @Transaction
    open suspend fun getFullMatch(playType: Int, tournamentId: Int, page: Int, startTime: Long): List<MatchWithMarkets> {
        return queryAllMatch(playType, tournamentId, page, startTime).map { matchBean ->
            val markets = geMarkets(matchBean.matchId).map { marketBean ->
                val selections = specialHandling(
                    marketBean.marketId,
                    getSelectionLites(matchBean.matchId, marketBean.marketId)
                )

                MarketWithSelections(marketBean, selections)
            }
            MatchWithMarkets(matchBean, markets)
        }
    }
    //針對market id不同selection做些特殊處理
    private fun specialHandling(marketId: Long, originSelections: List<SelectionBeanLite>): List<SelectionBeanLite> {
        return if (marketId == 1L && originSelections.size == 3) {
            originSelections
                .toMutableList()
                .apply {
                    this[1] = this[2].also { this[2] = this[1] }
                }
        } else { originSelections }
    }

    @Transaction
    open suspend fun getOneMatchByIds(matchId: List<Long>): List<MatchWithMarkets> {
        return getMatchByIds(matchId).map { matchBean ->
            val markets = geMarkets(matchBean.matchId).map { marketBean ->
                val selections = specialHandling(
                    marketBean.marketId,
                    getSelectionLites(matchBean.matchId, marketBean.marketId)
                )
                MarketWithSelections(marketBean, selections)
            }
            MatchWithMarkets(matchBean, markets)
        }
    }

    @Transaction
    open suspend fun getOneMatchById(matchId: Long): MatchWithMarkets {

        return getMatchById(matchId).let { matchBean ->
            val markets = geMarkets(matchBean.matchId).map { marketBean ->
                val selections = specialHandling(
                marketBean.marketId,
                getSelectionLites(matchBean.matchId, marketBean.marketId)
            )
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
