package arch.cayenne.lib.database.dao

import androidx.room.Dao
import androidx.room.Query
import arch.cayenne.lib.database.entity.HomeSelectedBean

@Dao
abstract class HomeSelectedDao: BaseDao<HomeSelectedBean>() {

    @Query(
        "SELECT * " +
        "FROM HomeSelectedBean " +
        "WHERE playType = :playType"
    )
    abstract fun queryHomeSelectedData(playType: Int): HomeSelectedBean?
}