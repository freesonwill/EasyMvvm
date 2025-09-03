package arch.cayenne.lib.database.dao

import androidx.room.Dao
import androidx.room.Query
import arch.cayenne.lib.database.entity.BetSlipOrderBean
import kotlinx.coroutines.flow.Flow

@Dao
abstract class BetSlipOrderDao : BaseDao<BetSlipOrderBean>() {

    @Query("SELECT * FROM BetSlipOrderBean WHERE betId = :betId LIMIT 1")
    abstract suspend fun getOrderBeanById(betId: String): BetSlipOrderBean?

    @Query("SELECT * FROM BetSlipOrderBean WHERE betSlipType = :type and liveMatchId = :matchId ORDER BY betTime DESC")
    abstract fun observeOrderBeanByMatchId(type: Int, matchId: Long = -1L): Flow<List<BetSlipOrderBean>>

    suspend fun deleteMissing(type: Int, keepIds: List<String>) {
        deleteMissingByMatchId(type, keepIds, -1L)
    }

    suspend fun deleteByType(type: Int) {
        deleteByTypeAndMatchId(type, -1L)
    }

    @Query("DELETE FROM BetSlipOrderBean WHERE betSlipType = :type and liveMatchId = :matchId AND betId NOT IN (:keepIds)")
    abstract suspend fun deleteMissingByMatchId(type: Int, keepIds: List<String>, matchId: Long)

    @Query("DELETE FROM BetSlipOrderBean WHERE betSlipType = :type and liveMatchId = :matchId")
    abstract suspend fun deleteByTypeAndMatchId(type: Int, matchId: Long)

    @Query("UPDATE BetSlipOrderBean SET settleStatus = :settleStatus WHERE betId = :betId")
    abstract suspend fun updateToPendingEarlySettle(betId: String, settleStatus: Int = 102)

    @Query("UPDATE BetSlipOrderBean SET earlySupport = 0 WHERE betId = :betId")
    abstract suspend fun updateCannotEarlySettle(betId: String)

}