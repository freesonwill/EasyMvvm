package arch.cayenne.lib.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import arch.cayenne.lib.database.entity.MessageBean
import kotlinx.coroutines.flow.Flow

@Dao
abstract class MessageDao : BaseDao<MessageBean>() {

    //查询所有消息,按时间降序排列
    @Query("SELECT * FROM MessageBean ORDER BY time DESC")
    abstract fun observeMessageBean(): Flow<List<MessageBean>>

    //查询监听最新的两条消息,按时间降序排列
    @Query("SELECT * FROM MessageBean ORDER BY time DESC limit 2")
    abstract fun observeLatestMessage(): Flow<List<MessageBean>>

    //根据ID查询消息
    @Query("SELECT * FROM MessageBean WHERE id = :id")
    abstract fun queryMessageById(id: Long): MessageBean?

    //根据ID删除消息
    @Query("DELETE FROM MessageBean WHERE id = :id")
    abstract suspend fun deleteMessageById(id: Long)

    //插入消息列表
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertMessage(message: List<MessageBean>)

    //更新消息的已读状态
    @Query("UPDATE MessageBean set status = :status where id = :id")
    abstract fun updateMessageStatus(status: Int, id: Long)
}