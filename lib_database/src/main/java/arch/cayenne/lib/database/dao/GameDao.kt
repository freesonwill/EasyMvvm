package arch.cayenne.lib.database.dao

import androidx.room.Dao
import androidx.room.Query
import arch.cayenne.lib.database.entity.GameBean

@Dao
abstract class GameDao : BaseDao<GameBean>() {

    //查询
    @Query("SELECT * FROM GameBean WHERE id = :id")
    abstract suspend fun queryGameBean(id: Int): GameBean

    //监听
    @Query("SELECT * FROM GameBean WHERE id = :id")
    abstract suspend fun observeGameBean(id: Int): GameBean

    //设置
    @Query("UPDATE GameBean SET clickFlag = :flag WHERE id = :id")
    abstract fun setGameBean(id: Int, flag: Int)

}