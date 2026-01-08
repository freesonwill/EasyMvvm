package arch.cayenne.module.account.data.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.database.GameDatabase
import arch.cayenne.lib.http.HttpClient
import arch.cayenne.lib.websocket.WebSocketManager
import kotlinx.coroutines.CoroutineScope


/**
 * 登录或注册的仓库类，用于处理与用户登录或注册相关的数据操作。
 *
 * @property scope 协程作用域，用于执行异步任务。
 * @property socketManager WebSocket 管理器，用于处理 WebSocket 连接。
 * @property userDataManager 用户数据管理器，用于管理用户数据。
 * @property database 游戏数据库实例，用于访问和操作本地数据库。
 * @property httpClient HTTP 客户端，用于执行网络请求。
 */
class LoginOrRegisterRepository(
    override val scope: CoroutineScope,
    private val socketManager: WebSocketManager,
    private val userDataManager: UserDataManager,
    private val database: GameDatabase,
    private val httpClient: HttpClient,
) : BaseRepository() {


}