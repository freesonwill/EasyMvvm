package arch.cayenne.lib.database.dao

import androidx.room.Dao
import androidx.room.Query
import arch.cayenne.lib.database.entity.ExampleBean

/**
 * @author: zhangsan
 * @date: 2025/4/14 10:37
 * @description:
 */
@Dao
abstract class ExampleDao : BaseDao<ExampleBean>() {

    @Query("delete from ExampleBean")
    abstract suspend fun deleteAll()

    @Query("DELETE FROM ExampleBean WHERE id = :id")
    abstract suspend fun remove(id: Int)

}