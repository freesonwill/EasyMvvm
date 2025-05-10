package arch.cayenne.lib.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import arch.cayenne.lib.database.entity.BetBean
import arch.cayenne.lib.database.entity.LiveVideoBean
import arch.cayenne.lib.database.entity.MarketTypeBean
import kotlinx.coroutines.flow.Flow

@Dao
abstract class MarketTypeBeanDao : BaseDao<MarketTypeBean>(){

    @Query("SELECT * FROM MarketTypeBean")
    abstract fun observeMarketTypeBean(): Flow<List<MarketTypeBean>>

    @Query("SELECT * FROM MarketTypeBean WHERE code IN (:code, :nowCode)")
    abstract fun getMarketTypeByCode(code: String, nowCode: String): List<MarketTypeBean>

    //查询所有
    @Query("SELECT * FROM MarketTypeBean")
    abstract suspend fun getAllMarketTypeBean(): List<MarketTypeBean>

    //删除所有
    @Query("delete from MarketTypeBean")
    abstract suspend fun deleteAll()

}