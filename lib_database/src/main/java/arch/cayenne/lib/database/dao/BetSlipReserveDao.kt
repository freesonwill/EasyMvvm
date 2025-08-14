package arch.cayenne.lib.database.dao

import androidx.room.Dao
import androidx.room.Query
import arch.cayenne.lib.database.entity.BetSlipReserveBean
import kotlinx.coroutines.flow.Flow

@Dao
abstract class BetSlipReserveDao : BaseDao<BetSlipReserveBean>() {

    @Query("SELECT * FROM BetSlipReserveBean WHERE liveMatchId = :matchId ORDER BY reserveTime DESC")
    abstract fun observeReserveBeanByMatchId(matchId: Long): Flow<List<BetSlipReserveBean>>

    @Query("UPDATE BetSlipReserveBean SET odds = :newOdds WHERE reserveId = :reserveId")
    abstract suspend fun updateOdds(reserveId: String, newOdds: Int)

    @Query("DELETE FROM BetSlipReserveBean WHERE reserveId = :reserveId")
    abstract suspend fun deleteById(reserveId: String)

    suspend fun deleteMissing(keepIds: List<String>) {
        deleteMissingByMatchId(keepIds, -1L)
    }

    suspend fun deleteAll() {
        deleteAllByMatchId(-1L)
    }

    @Query("DELETE FROM BetSlipReserveBean WHERE liveMatchId = :matchId and reserveId NOT IN (:keepIds)")
    abstract suspend fun deleteMissingByMatchId(keepIds: List<String>, matchId: Long)

    @Query("DELETE FROM BetSlipReserveBean WHERE liveMatchId = :matchId")
    abstract suspend fun deleteAllByMatchId(matchId: Long)
}