package arch.cayenne.lib.database.dao

import androidx.room.Dao
import androidx.room.Query
import arch.cayenne.lib.database.entity.ShowType
import arch.cayenne.lib.database.entity.SportBean
import arch.cayenne.lib.database.entity.SportDataModel
import arch.cayenne.lib.database.entity.SportLiteBean

@Dao
abstract class SportDao : BaseDao<SportBean>() {
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    abstract fun insertSportCrossRef(data: List<PlayTypeSportCrossRef>): List<Long>
//
//    @Query("SELECT * " +
//            "FROM PlayTypeSportCrossRef " +
//            "WHERE playType = :playType order by sportOrder"
//    )
//    abstract fun querySportsMatchCount(playType: Int): List<PlayTypeSportCrossRef>

    @Query("SELECT bean.sportId as id, bean.matchCount as matchCount, bean.sportOrder as `order` " +
            "FROM SportBean bean WHERE bean.type = :type order by sportOrder")
    abstract fun querySportsMatchCount(type: ShowType = ShowType.HOME): List<SportDataModel>

    @Query("DELETE FROM SportBean WHERE type = :type")
    abstract fun clearSports(type: ShowType = ShowType.HOME)

    @Query("SELECT sportId, sportName FROM SportBean WHERE type = :type")
    abstract fun getAllSports(type: ShowType = ShowType.ALL): List<SportLiteBean>

    @Query("SELECT sportId, sportName FROM SportBean WHERE sportId IN (:ids) AND type = :type")
    abstract fun getSportByIds(ids: List<Int>, type: ShowType = ShowType.ALL): List<SportLiteBean>
}