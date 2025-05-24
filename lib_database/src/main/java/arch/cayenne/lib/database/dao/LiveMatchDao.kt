package arch.cayenne.lib.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import arch.cayenne.lib.database.entity.LiveMarketBean
import arch.cayenne.lib.database.entity.LiveMatchBean
import arch.cayenne.lib.database.entity.LiveSelectionBean
import arch.cayenne.lib.database.entity.LiveSelectionBeanRecord
import kotlinx.coroutines.flow.Flow

@Dao
abstract class LiveMatchDao : BaseDao<LiveMatchBean>() {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertMatch(match: List<LiveMatchBean>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertMarkets(markets: List<LiveMarketBean>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertSelections(selections: List<LiveSelectionBean>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertSelectionsRecord(selections: List<LiveSelectionBeanRecord>)

    @Transaction
    @Query("SELECT * FROM LiveMatchBean WHERE matchId = :matchId")
    abstract suspend fun getMatchById(matchId: Long): LiveMatchBean

    @Query("SELECT * FROM LiveMatchBean WHERE matchId = :matchId")
    abstract fun observeMatchById(matchId: Long): Flow<LiveMatchBean>

    @Query("SELECT * FROM LiveSelectionBean WHERE marketId IN (:marketIds)")
    abstract fun observeSelectionByIds(marketIds: List<Long>): Flow<List<LiveSelectionBean>>

    @Transaction
    @Query("SELECT * FROM LiveMatchBean WHERE matchId IN (:matchIds)")
    abstract suspend fun getMatchByIds(matchIds: List<Long>): List<LiveMatchBean>


    @Transaction
    @Query("SELECT * FROM LiveSelectionBean WHERE marketId = :marketId")
    abstract suspend fun getSelectionById(marketId: Long): LiveSelectionBean

    @Transaction
    @Query("SELECT * FROM LiveSelectionBean WHERE selectionId = :selectionId")
    abstract suspend fun getSelectionBySelectionId(selectionId: Long): LiveSelectionBean

    @Transaction
    @Query("SELECT * FROM LiveSelectionBean WHERE marketId =:marketId")
    abstract suspend fun getSelectionsByIds(marketId: Long): List<LiveSelectionBean>

    @Transaction
    @Query("SELECT * FROM LiveSelectionBeanRecord")
    abstract suspend fun getSelectionsRecord(): List<LiveSelectionBeanRecord>

    @Query("DELETE FROM LiveMatchBean")
    abstract fun deleteMatchBean()

    @Query("DELETE FROM LiveMarketBean")
    abstract fun deleteMarketBean()

    @Query("DELETE FROM LiveSelectionBean")
    abstract fun deleteSelectionBean()

    @Query("DELETE FROM LiveSelectionBeanRecord")
    abstract fun deleteSelectionBeanRecord()


    @Query("DELETE FROM LiveSelectionBean WHERE selectionId IN (:selectionsIds)")
    abstract fun deleteSelectionBeanById( selectionsIds: List<Long>)

    @Query("DELETE FROM LiveSelectionBeanRecord WHERE selectionId IN (:selectionsIds)")
    abstract fun deleteSelectionBeanRecordById(selectionsIds: List<Long>)



    //收到notify更新数据
    @Query(
        "UPDATE LiveMatchBean " +
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
                "WHERE matchId = :matchId"
    )
    abstract fun updateNotifyMatchInfo(
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


    @Transaction
    open suspend fun insertFullMatch(
        matches: List<LiveMatchBean>,
        markets: List<LiveMarketBean>,
        selections: List<LiveSelectionBean>,
        selectionsRecord :List<LiveSelectionBeanRecord>,
    ) {
        insertMatch(matches)
        insertMarkets(markets)
        insertSelections(selections)
        insertSelectionsRecord(selectionsRecord)
    }

    @Transaction
    open suspend fun updateLiveSelectionBean(
        selections: List<LiveSelectionBean>,
        selectionsRecord: List<LiveSelectionBeanRecord>,
        selectionsDeleteIds: List<Long>,
    ) {
        if (selections.isNotEmpty()) {
            insertSelections(selections)
        }
        if (selectionsRecord.isNotEmpty()) {
            insertSelectionsRecord(selectionsRecord)
        }
        if (selectionsDeleteIds.isNotEmpty()){
            deleteSelectionBeanById(selectionsDeleteIds)
            deleteSelectionBeanRecordById(selectionsDeleteIds)
        }
    }

    @Transaction
    open fun clearAllMatch() {
        deleteMatchBean()
        deleteMarketBean()
        deleteSelectionBean()
        deleteSelectionBeanRecord()
    }
}
