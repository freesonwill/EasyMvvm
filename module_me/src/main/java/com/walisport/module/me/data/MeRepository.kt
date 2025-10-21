package com.walisport.module.me.data

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.database.GameDatabase
import arch.cayenne.lib.database.dao.MessageDao
import arch.cayenne.lib.websocket.WebSocketManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn

class MeRepository(
    override val scope: CoroutineScope,
    private val socketManager: WebSocketManager,
    private val manager: UserDataManager,
    private val database: GameDatabase,
    ) : BaseRepository() {

    fun observeUnReadMsg() = database.msgDao().observeMessageBeanByStatus(MessageDao.STATUS_UNREAD).flowOn(Dispatchers.IO)


}