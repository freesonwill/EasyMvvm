package com.walisport.module.gamedetail.data

import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.loge
import arch.cayenne.lib.common.data.constants.PreloadEnum
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.common.utils.ext.ccyToSymbol
import arch.cayenne.lib.common.utils.ext.symbolUrl
import arch.cayenne.lib.database.GameDatabase
import arch.cayenne.lib.http.HttpClient
import arch.cayenne.lib.http.HttpException
import arch.cayenne.lib.websocket.WebSocketManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * 游戏详情的Repository
 */
class GameDetailRepository(
    override val scope: CoroutineScope ,
    private val database: GameDatabase ,
    private val httpClient: HttpClient ,
    private val mockHttpClient: HttpClient ,
    private val socketManager: WebSocketManager ,
    private val preloadResultChange: MutableStateFlow<PreloadEnum> ,
    private val manager: UserDataManager ,
) : BaseRepository() {


    suspend fun getGameDetail(id: Int): ApiResponseState {
        val api = mockHttpClient.create(IGameDetailApi::class.java)
        return suspendCancellableCoroutine<ApiResponseState> { cancellableContinuation ->
            scope.launch(Dispatchers.IO) {
                mockHttpClient.safeRequest(
                    request = {
                        api.getGameDetail(id)
                    } ,
                    onSuccess = { resp ->
                        if (resp.code == 0) {
                            cancellableContinuation.resume(ApiResponseState.Succeeded(resp.data))
                        } else {
                            "response------>${resp.code},${resp.message}".loge(TAG)
                            cancellableContinuation.resume(
                                ApiResponseState.Failed(
                                    HttpException(
                                        resp.code ,
                                        resp.message
                                    )
                                )
                            )
                        }
                    } ,
                    onFailure = { code , msg , throwable ->
                        "response------>$code,$msg,$throwable".loge(TAG)
                        cancellableContinuation.resume(
                            ApiResponseState.Failed(
                                HttpException(
                                    code ,
                                    msg ?: ""
                                )
                            )
                        )
                    }
                )
            }
        }
    }


    companion object {
        const val DEFAULT_PAGE_SIZE = 10
        const val INITIAL_PAGE = 1
    }


}