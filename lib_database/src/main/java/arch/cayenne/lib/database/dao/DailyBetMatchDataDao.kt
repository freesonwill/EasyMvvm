package arch.cayenne.lib.database.dao

import androidx.room.Dao
import androidx.room.Query
import arch.cayenne.lib.database.entity.DailyBetMatchDataBean
import kotlinx.coroutines.flow.Flow

/**
 *
 * @date: 2026/1/3 21:34
 * @description:
 */
@Dao
abstract class DailyBetMatchDataDao : BaseDao<DailyBetMatchDataBean>() {

    /**
     * 观察数据库中 `DailyBetMatchDataBean` 表的第一条记录。
     * 返回一个 `Flow`，用于实时监听数据变化。
     *
     * @return 一个 `Flow` 对象，包含 `DailyBetMatchDataBean?` 类型的数据，
     *         如果表中没有数据则返回 `null`。
     */
    @Query("SELECT * FROM DailyBetMatchDataBean LIMIT 1")
    abstract fun observeDailyBetMatchDataBean(): Flow<DailyBetMatchDataBean?>


    /**
     * 检查数据库中是否存在 `DailyBetMatchDataBean` 表的记录。
     *
     * @return 如果表中存在至少一条记录，则返回 `true`；否则返回 `false`。
     */
    @Query("SELECT COUNT(*) > 0 FROM DailyBetMatchDataBean")
    abstract suspend fun hasDailyBetMatchDataBean(): Boolean

}