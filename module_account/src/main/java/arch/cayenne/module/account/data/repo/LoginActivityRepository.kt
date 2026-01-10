package arch.cayenne.module.account.data.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.common.data.constants.SkinType
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.database.dao.InfoDao
import arch.cayenne.lib.database.dao.MessageDao
import arch.cayenne.lib.database.dao.SportDao
import arch.cayenne.lib.database.entity.MessageBean
import arch.cayenne.lib.database.entity.ShowType
import arch.cayenne.lib.database.entity.SportBean
import arch.cayenne.lib.websocket.WebSocketManager
import arch.cayenne.lib.websocket.data.ApiCode
import arch.cayenne.lib.websocket.extension.observeProtoMessage
import arch.cayenne.lib.websocket.extension.sendAndWaitProtoMessageResponse
import galaxy.client.proto.Client
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class LoginActivityRepository(
    override val scope: CoroutineScope,
    private val userDataManager: UserDataManager,
    private val socketManager: WebSocketManager,
    private val sportDao: SportDao,
    private val msgDao: MessageDao,
    private val infoDao: InfoDao,
) : BaseRepository() {

    //获取皮肤背景
    fun getSkinType(): String {
        return userDataManager.getValue(UserDataKey.KEY_SKIN, SkinType.DEFAULT)
    }

}