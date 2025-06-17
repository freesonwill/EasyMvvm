package com.walisport.module.message.data

import arch.cayenne.lib.base.data.repository.BaseRepository
import com.walisport.module.message.MessageRemoteManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class MessageMainRepository(
    private val remoteManager: MessageRemoteManager
) : BaseRepository() {

    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    //获取用户消息列表
    suspend fun getUserMessageList(id: Long): List<NotificationBean> {
        val resp = remoteManager.getUserMessageListReq(scope, id)
        val list = ArrayList<NotificationBean>()
        resp?.msgRecordList?.mapIndexed { _, item ->
            val temp = NotificationBean(
                id = item.id,                //消息ID
                type = item.type,            //消息类型 1系统通知 2活动通知
                state = item.status,         //状态 0未读 1已读
                title = item.title,          //标题
                content = item.content,      //内容
                createTime = item.createTime //创建时间
            )
            list.add(temp)
        }
        return list
    }
}

