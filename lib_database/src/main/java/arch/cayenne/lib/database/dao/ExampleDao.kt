package arch.cayenne.lib.database.dao

import androidx.room.Dao
import androidx.room.Query

/**
 * @author: zhangsan
 * @date: 2025/4/14 10:37
 * @description:
 */
@Dao
abstract class ExampleDao : BaseDao<ExampleDao>() {

    @Query("delete from SimpleBean")
    abstract suspend fun deleteAll()

    @Query("DELETE FROM SimpleBean WHERE id = :id")
    abstract suspend fun remove(id: Int)

}