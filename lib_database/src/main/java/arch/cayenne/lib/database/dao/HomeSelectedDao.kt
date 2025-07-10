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

    @Query("UPDATE HomeSelectedBean " +
            "SET sportId = :sportId " +
            "WHERE playType = :playTypeId ")
    abstract fun updateSportId(playTypeId: Int, sportId: Int)

    @Query("DELETE FROM HomeSelectedBean")
    abstract fun clearAllHomeSelectedData()
}