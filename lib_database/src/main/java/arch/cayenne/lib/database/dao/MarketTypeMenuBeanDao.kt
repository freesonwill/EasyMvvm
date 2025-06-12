package arch.cayenne.lib.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import arch.cayenne.lib.database.entity.BetBean
import arch.cayenne.lib.database.entity.LiveVideoBean
import arch.cayenne.lib.database.entity.MarketMenuBean
import arch.cayenne.lib.database.entity.MarketTypeBean
import kotlinx.coroutines.flow.Flow

@Dao
abstract class MarketTypeMenuBeanDao : BaseDao<MarketMenuBean>(){

    @Query("SELECT * FROM MarketMenuBean")
    abstract fun observeMarketTypeBean(): Flow<List<MarketMenuBean>>

    @Query("SELECT * FROM MarketMenuBean WHERE code = :code")
    abstract fun getMarketMenuByCode(code: String): List<MarketMenuBean>

    //查询所有
    @Query("SELECT * FROM MarketMenuBean")
    abstract suspend fun getAllMarketMenuAll(): List<MarketMenuBean>

    //删除所有
    @Query("delete from MarketMenuBean")
    abstract suspend fun deleteAll()

    @Query("SELECT MAX(number) FROM MarketMenuBean")
    abstract suspend fun getMaxOrderNumber(): Int?
}