package arch.cayenne.lib.database.dao

import androidx.room.Dao
import androidx.room.Query
import arch.cayenne.lib.database.entity.BetBean
import arch.cayenne.lib.database.entity.LiveVideoBean
import arch.cayenne.lib.database.entity.MarketTypeBean
import kotlinx.coroutines.flow.Flow

@Dao
abstract class MarketTypeBeanDao : BaseDao<MarketTypeBean>(){

//    @Query("""
//    UPDATE MarketTypeBean
//    SET isSelect = CASE  WHEN marketId = :marketId THEN :marketSelect
//        WHEN marketId = :selectId THEN :isSelect
//        ELSE isSelect
//    END
//    WHERE marketId IN (:marketId, :selectId)""")
//    abstract fun observeMarketTypeBeanDao(marketId: Long, marketSelect: Boolean, selectId: Long = 0, isSelect: Boolean = false)



    @Query("""
    UPDATE MarketTypeBean
    SET isSelect = CASE  WHEN marketId = :marketId THEN :marketSelect
        WHEN marketId = :selectId THEN :isSelects
        ELSE isSelect
    END
    WHERE marketId IN (:marketId, :selectId)""")
    abstract suspend fun updateMarketIdByMarketSelect(marketId: Long, marketSelect: Boolean, selectId: Long , isSelects: Boolean):Int

    //查询所有
    @Query("SELECT * FROM MarketTypeBean")
    abstract suspend fun getAllMarketTypeBean(): List<MarketTypeBean>

    //删除所有
    @Query("delete from MarketTypeBean")
    abstract suspend fun deleteAll()

}