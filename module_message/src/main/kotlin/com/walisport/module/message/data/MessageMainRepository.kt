package com.walisport.module.message.data

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.database.dao.MessageDao
import arch.cayenne.lib.database.entity.MessageBean
import com.walisport.module.message.MessageRemoteManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MessageMainRepository(
    private val remoteManager: MessageRemoteManager,
    private val msgDao: MessageDao,
) : BaseRepository() {

    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    //监听数据表变化
    fun observeMessageBean() = msgDao.observeMessageBean()
    //监听最新的两条消息,按时间降序排列
    fun observeLatestMessage() = msgDao.observeLatestMessage()

    //插入消息列表
    private fun insertMessage(message: List<NotificationBean>) {
        scope.launch {
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
    }

    //获取用户消息列表
    fun getMessageList(id: Long, type: Int): List<NotificationBean> {
        val list = ArrayList<NotificationBean>()
        scope.launch {
            val resp = remoteManager.getUserMessageListReq(scope, id, type)
            resp?.msgRecordList?.mapIndexed { _, item ->
                val temp = NotificationBean(
                    id = item.id,                 //消息ID
                    type = item.type,             //消息类型 1系统通知 2活动通知
                    state = item.status,          //状态 0未读 1已读
                    title = item.title,           //标题
                    content = item.content,       //内容
                    createTime = item.createTime  //创建时间
                )
                list.add(temp)
            }
            insertMessage(list)
        }
        return list
    }

    //修改消息状态，删除或将消息设为已读 status = 1未读 2已读 3删除
    fun updateMessageStatus(id: Long, status: Int) {
        scope.launch {
            remoteManager.updateMessageStatus(scope, id, status)
            if (status == 2) {
                msgDao.updateMessageStatus(status, id)
            } else {
                msgDao.deleteMessageById(id)
            }
        }
    }
}

