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

    fun observeMessageBean() = msgDao.observeMessageBean()

    suspend fun insertMessage(message: List<NotificationBean>){
        val temp = message.mapIndexed { _, item ->
            MessageBean(
                id = item.id,
                type = item.type,
                title = item.title,
                content = item.content,
                status = 1,
                time = 1750145046L
            )
        }
        msgDao.insertMessage(temp)
    }
}

