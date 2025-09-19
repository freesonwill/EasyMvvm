package arch.cayenne.lib.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author: wenxi
 * @date: 17/9/25 17:32
 * @description: 保存收藏赛事id
 */
@Entity(tableName = "collect_list")
data class CollectListBean(
    @PrimaryKey
    val matchId:Long,
    val startTime: Long,
    val page: Int,
    val order: Int,
)


