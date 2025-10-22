package arch.cayenne.lib.common.data.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.database.GameDatabase
import arch.cayenne.lib.database.dao.MessageDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn

/**
 *
 * @date: 2025/10/21 16:43
 * @description:
 */
class UnReadMessageRepository(
    override val scope: CoroutineScope,
    private val database: GameDatabase,
) : BaseRepository() {
    fun observeUnReadMsg() =
        database.msgDao().observeMessageBeanByStatus(MessageDao.STATUS_UNREAD).flowOn(
            Dispatchers.IO
        )

}