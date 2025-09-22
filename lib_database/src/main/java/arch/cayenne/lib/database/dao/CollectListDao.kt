package arch.cayenne.lib.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import arch.cayenne.lib.database.entity.CollectListBean
import kotlinx.coroutines.flow.Flow

/**
 * @author: wenxi
 * @date: 17/9/25 17:35
 * @description:
 */
@Dao
abstract class CollectListDao :BaseDao<CollectListBean>(){

    @Insert(onConflict = OnConflictStrategy.REPLACE)
   abstract fun insertCollect(id:CollectListBean)

   @Insert(onConflict = OnConflictStrategy.REPLACE)
   abstract fun insertCollects(ids:List<CollectListBean>)

   @Query("DELETE FROM collect_list WHERE matchId = :matchId")
   abstract fun deleteById(matchId:Long)

   @Query("DELETE FROM collect_list")
   abstract fun deleteAll()

   @Query("SELECT * FROM collect_list")
   abstract fun getAllCollectList():List<CollectListBean>

   @Query("SELECT * FROM collect_list")
   abstract fun observerCollectList(): Flow<CollectListBean>
}