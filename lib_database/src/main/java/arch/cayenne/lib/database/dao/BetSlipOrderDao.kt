package arch.cayenne.lib.database.dao

import androidx.room.Dao
import androidx.room.Query
import arch.cayenne.lib.database.entity.BetSlipOrderBean
import kotlinx.coroutines.flow.Flow

@Dao
abstract class BetSlipOrderDao : BaseDao<BetSlipOrderBean>() {

    @Query("SELECT * FROM BetSlipOrderBean WHERE betId = :betId LIMIT 1")
    abstract suspend fun getOrderBeanById(betId: String): BetSlipOrderBean?

    @Query("SELECT * FROM BetSlipOrderBean WHERE betSlipType = :type ORDER BY betTime DESC")
    abstract fun observeOrderBean(type: Int): Flow<List<BetSlipOrderBean>>

    @Query("DELETE FROM BetSlipOrderBean WHERE betId = :betId")
    abstract suspend fun deleteById(betId: String)

    @Query("DELETE FROM BetSlipOrderBean WHERE betSlipType = :type AND betId NOT IN (:keepIds)")
    abstract suspend fun deleteMissing(type: Int, keepIds: List<String>)

    @Query("DELETE FROM BetSlipOrderBean WHERE betSlipType = :type")
    abstract suspend fun deleteByType(type: Int)

    @Query("UPDATE BetSlipOrderBean SET settleStatus = :settleStatus WHERE betId = :betId")
    abstract suspend fun updateToPendingEarlySettle(betId: String, settleStatus: Int = 102)

    @Query("UPDATE BetSlipOrderBean SET betSlipType = :type WHERE betId = :betId")
    abstract suspend fun updateBetSlipType(betId: String, type: Int)
}