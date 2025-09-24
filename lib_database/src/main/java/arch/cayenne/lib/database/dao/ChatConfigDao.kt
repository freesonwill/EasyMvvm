package arch.cayenne.lib.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import arch.cayenne.lib.database.entity.ChatConfigBean

/**
 * @author: wenxi
 * @date: 24/9/25 10:57
 * @description:
 */
@Dao
abstract class ChatConfigDao : BaseDao<ChatConfigBean>() {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertChatConfig(config:ChatConfigBean)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract fun updateConfigBean(config: ChatConfigBean)

    @Query("SELECT * FROM ChatConfigBean order by id asc LIMIT 1")
    abstract fun getFirst():ChatConfigBean?

}