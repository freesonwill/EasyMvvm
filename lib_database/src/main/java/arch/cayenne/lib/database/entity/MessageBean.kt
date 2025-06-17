package arch.cayenne.lib.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MessageBean(
    @PrimaryKey val id: Long, //消息ID
    val type: Int,            //消息类型 1系统通知 2活动通知
    val status: Int,          //状态 0未读 1已读
    val title: String,        //消息标题
    val content: String,      //消息内容
    val time:Long,            //创建时间
)