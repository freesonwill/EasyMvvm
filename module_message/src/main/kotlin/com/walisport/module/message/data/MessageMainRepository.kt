package com.walisport.module.message.data

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.database.dao.MessageDao
import arch.cayenne.lib.database.entity.MessageBean
import com.walisport.module.message.MessageRemoteManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class MessageMainRepository(
    private val remoteManager: MessageRemoteManager,
    private val msgDao: MessageDao,
) : BaseRepository() {
    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    //监听数据表变化
    fun observeMessageBean() = msgDao.observeMessageBean()

    //插入消息列表
    private suspend fun insertMessage(message: List<NotificationBean>) {
        val temp = message.mapIndexed { _, item ->
            MessageBean(
                id = item.id,
                type = item.type,
                title = item.title,
                content = item.content,
                status = item.state,
                time = item.createTime
            )
        }
        msgDao.insertMessage(temp)
    }

    //获取用户消息列表
    suspend fun getMessageList(id: Long): List<NotificationBean> {
        val resp = remoteManager.getUserMessageListReq(scope, id)
        val list = ArrayList<NotificationBean>()
       /* resp?.msgRecordList?.mapIndexed { _, item ->
            val temp = NotificationBean(
                id = item.id,                 //消息ID
                type = item.type,             //消息类型 1系统通知 2活动通知
                state = item.status,          //状态 0未读 1已读
                title = item.title,           //标题
                content = item.content,       //内容
                createTime = item.create_time //创建时间
            )
            list.add(temp)
        }
        insertMessage(list)*/
        return list
    }

    //修改消息状态，删除或将消息设为已读 status = 0未读 1已读 2删除
    suspend fun updateMessageStatus(id: Long, status: Int) {
        remoteManager.updateMessageStatus(scope, id, status)
    }
}

