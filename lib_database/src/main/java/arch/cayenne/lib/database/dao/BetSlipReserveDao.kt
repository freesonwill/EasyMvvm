package arch.cayenne.lib.database.dao

import androidx.room.Dao
import androidx.room.Query
import arch.cayenne.lib.database.entity.BetSlipReserveBean
import kotlinx.coroutines.flow.Flow

@Dao
abstract class BetSlipReserveDao : BaseDao<BetSlipReserveBean>() {

    @Query("SELECT * FROM BetSlipReserveBean ORDER BY reserveTime DESC")
    abstract fun observeReserveBean(): Flow<List<BetSlipReserveBean>>

    @Query("UPDATE BetSlipReserveBean SET odds = :newOdds WHERE reserveId = :reserveId")
    abstract suspend fun updateOdds(reserveId: String, newOdds: String)

    @Query("DELETE FROM BetSlipReserveBean WHERE reserveId = :reserveId")
    abstract suspend fun deleteById(reserveId: String)

    @Query("DELETE FROM BetSlipReserveBean WHERE reserveId NOT IN (:keepIds)")
    abstract suspend fun deleteMissing(keepIds: List<String>)

    @Query("DELETE FROM BetSlipReserveBean")
    abstract suspend fun deleteAll()
}