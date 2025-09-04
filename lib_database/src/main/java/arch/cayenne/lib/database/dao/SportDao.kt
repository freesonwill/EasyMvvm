package arch.cayenne.lib.database.dao

import androidx.room.Dao
import androidx.room.Query
import arch.cayenne.lib.database.entity.ShowType
import arch.cayenne.lib.database.entity.SportBean
import arch.cayenne.lib.database.entity.SportDataModel
import arch.cayenne.lib.database.entity.SportLiteBean
import kotlinx.coroutines.flow.Flow

@Dao
abstract class SportDao : BaseDao<SportBean>() {

    @Query("SELECT bean.sportId as id, bean.matchCount as matchCount, bean.type as type, bean.sportOrder as `order`, 0 as isSelected " +
            "FROM SportBean bean WHERE bean.sportId in (:filter) order by sportOrder")
    abstract fun observeSportsMatchCount(filter: List<Int>): Flow<List<SportDataModel>>

    @Query("SELECT sportId, sportName FROM SportBean WHERE type = :type")
    abstract fun observeAllSports(type: ShowType = ShowType.ALL): Flow<List<SportLiteBean>>

    @Query("SELECT sportId, sportName FROM SportBean WHERE type = :type")
    abstract fun getAllSports(type: ShowType = ShowType.ALL): List<SportLiteBean>

    @Query("SELECT sportId, sportName FROM SportBean WHERE sportId IN (:ids) AND type = :type")
    abstract fun getSportByIds(ids: List<Int>, type: ShowType = ShowType.ALL): List<SportLiteBean>

    @Query("SELECT * FROM SportBean WHERE sportId = :id AND type = :type")
    abstract fun getSportById(id: Int, type: ShowType = ShowType.ALL): SportBean?

    @Query("DELETE FROM SportBean WHERE sportId NOT IN (:keepIds)")
    abstract suspend fun deleteMissing(keepIds: List<Int>)

    @Query("DELETE FROM SportBean")
    abstract suspend fun clearSportBean()
}