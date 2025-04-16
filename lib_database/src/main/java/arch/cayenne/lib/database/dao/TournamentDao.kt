package arch.cayenne.lib.database.dao

import androidx.room.Dao
import arch.cayenne.lib.database.entity.TournamentBean

@Dao
abstract class TournamentDao: BaseDao<TournamentBean>() {
}