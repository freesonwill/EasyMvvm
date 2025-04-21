package arch.cayenne.lib.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import arch.cayenne.lib.database.entity.SportBean
import arch.cayenne.lib.database.entity.PlayTypeSportCrossRef

@Dao
abstract class SportDao : BaseDao<SportBean>() {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertSportCrossRef(data: List<PlayTypeSportCrossRef>): List<Long>

    @Query("SELECT * " +
            "FROM PlayTypeSportCrossRef " +
            "WHERE playType = :playType order by sportOrder"
    )
    abstract fun querySportsMatchCount(playType: Int): List<PlayTypeSportCrossRef>
}