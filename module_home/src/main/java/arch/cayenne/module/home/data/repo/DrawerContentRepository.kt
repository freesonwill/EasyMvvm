package arch.cayenne.module.home.data.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.database.dao.UserDataDao
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
    private val userDataManager: UserDataManager,
    private val userDataDao: UserDataDao,
) : BaseRepository() {
    fun getDefaultResId(): Int {
        // Retrieve the default resource ID for the drawer content
        return userDataManager.getValue(UserDataKey.KEY_PERSONAL_INFO_RES_ID, -1)
    }
    fun getDefaultNickName(): String {
        // Retrieve the default nickname for the user
        return userDataManager.getValue(UserDataKey.KEY_PERSONAL_INFO_NICKNAME, "")
    }

    /**
     * 检查用户是否已登录。
     *
     * 此方法通过检查用户数据管理器中存储的用户令牌（KEY_TOKEN）是否存在且非空，
     * 来判断用户的登录状态。
     *
     * @return `true` 如果用户令牌存在且非空，表示用户已登录；
     *         否则返回 `false`。
     */
    fun checkIsLogin(): Boolean {
        return !userDataManager.getValue<String>(UserDataKey.KEY_TOKEN).isNullOrEmpty()
    }

    fun observeUserInfo() = userDataDao.observeUser()
}