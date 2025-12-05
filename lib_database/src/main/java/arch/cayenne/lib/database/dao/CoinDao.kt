package arch.cayenne.lib.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import arch.cayenne.lib.database.entity.CoinBean
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CoinDao : BaseDao<CoinBean>() {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertCurrencyList(data: List<CoinBean>)

    @Query("SELECT * FROM CoinBean")
    abstract fun observeCurrency(): Flow<List<CoinBean>>
}