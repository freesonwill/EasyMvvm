package arch.cayenne.lib.database.dao

import androidx.room.Dao
import arch.cayenne.lib.database.entity.SportBean

@Dao
abstract class SportDao : BaseDao<SportBean>() {
//    @Query("SELECT * " +
//            "FROM sport_bean " +
//            "WHERE gameType = :gameType order by sportOrder")
//    abstract fun getSportsMatchCount(gameType: Int): List<SportBean>
//
//    @Query("SELECT sportId " +
//            "FROM sport_bean " +
//            "WHERE gameType = :gameType order by sportOrder limit 1")
//    abstract fun getDefaultSport(gameType: Int) : Int
}