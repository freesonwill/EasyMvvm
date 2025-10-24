package arch.cayenne.lib.common.utils.ext

import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * VIP 數據管理擴展
 * 提供 VIP 等級數據的讀寫和監聽功能
 */
object VIPDataExt : KoinComponent {

    private val userDataManager: UserDataManager by inject()

    /**
     * 設置 VIP 等級
     */
    fun setVIPLevel(level: Long) {
        userDataManager.setKeyValue(UserDataKey.KEY_VIP_LEVEL, level)
    }

    /**
     * 獲取 VIP 等級
     * @param default 默認值
     */
    fun getVIPLevel(default: Long = 0L): Long {
        return userDataManager.getValue(UserDataKey.KEY_VIP_LEVEL, default)
    }

    /**
     * 監聽 VIP 等級變化
     */
    fun observeVIPLevel(): Flow<Long> {
        return userDataManager.observe(UserDataKey.KEY_VIP_LEVEL)
    }
}

