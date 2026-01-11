package arch.cayenne.module.account.data.repo

import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.loge
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.database.GameDatabase
import arch.cayenne.lib.http.HttpClient
import arch.cayenne.lib.http.HttpException
import arch.cayenne.lib.websocket.WebSocketManager
import arch.cayenne.module.account.data.model.LoginVo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume


/**
 * 登录或注册的仓库类，用于处理与用户登录或注册相关的数据操作。
 *
 * @property scope 协程作用域，用于执行异步任务。
 * @property socketManager WebSocket 管理器，用于处理 WebSocket 连接。
 * @property userDataManager 用户数据管理器，用于管理用户数据。
 * @property database 游戏数据库实例，用于访问和操作本地数据库。
 * @property httpClient HTTP 客户端，用于执行网络请求。
 */
class AccountLoginRepository(
    override val scope: CoroutineScope,
    private val socketManager: WebSocketManager,
    private val userDataManager: UserDataManager,
    private val database: GameDatabase,
    private val httpClient: HttpClient,
) : BaseRepository() {


    suspend fun accountLogin(
        countryCode: String,
        phoneNumber: String,
        sms: String
    ): ApiResponseState {
        val api = httpClient.create(IAccountLoginApi::class.java)
        return suspendCancellableCoroutine<ApiResponseState> { cancellableContinuation ->
            scope.launch(Dispatchers.IO) {
                httpClient.safeRequest(
                    request = {
                        api.accountLogin(
                            loginVo = LoginVo(
                                countryCode = countryCode,
                                nationalNumber = phoneNumber,
                                pwd = "",
                                mobileType = android.os.Build.MODEL, // 获取设备型号
                                osVer = "",
                                jb = false,
                                sms = sms,
                                clientId = "",
                                authSecret = "",
                            )
                        )
                    },
                    onSuccess = { resp ->
                        if (resp.code == 0) {
                            cancellableContinuation.resume(ApiResponseState.Succeeded(resp.data))
                        } else {
                            "response------>${resp.code},${resp.message}".loge(TAG)
                            cancellableContinuation.resume(
                                ApiResponseState.Failed(
                                    HttpException(
                                        resp.code,
                                        resp.message
                                    )
                                )
                            )
                        }
                    },
                    onFailure = { code, msg, throwable ->
                        "response------>$code,$msg,$throwable".loge(TAG)
                        cancellableContinuation.resume(
                            ApiResponseState.Failed(
                                HttpException(
                                    code,
                                    msg ?: ""
                                )
                            )
                        )
                    }
                )
            }
        }
    }


}