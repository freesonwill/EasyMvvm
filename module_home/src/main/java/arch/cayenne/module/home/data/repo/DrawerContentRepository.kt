package arch.cayenne.module.home.data.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.websocket.WebSocketManager
import kotlinx.coroutines.CoroutineScope

/**
 * @author: ricky.chang
 * @date: 2025/6/12 下午6:25
 * @description:
 */
class DrawerContentRepository(
    override val scope: CoroutineScope,
    private val socketManager: WebSocketManager,
    private val userDataManager: UserDataManager
) : BaseRepository() {
    fun getDefaultResId(): Int {
        // Retrieve the default resource ID for the drawer content
        return userDataManager.getValue(UserDataKey.KEY_PERSONAL_INFO_RES_ID, -1)
    }
    fun getDefaultNickName(): String {
        // Retrieve the default nickname for the user
        return userDataManager.getValue(UserDataKey.KEY_PERSONAL_INFO_NICKNAME, "")
    }
}