package com.walisport.module.business.common.data

import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.loge
import arch.cayenne.lib.common.data.constants.PreloadEnum
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.database.GameDatabase
import arch.cayenne.lib.database.entity.GameBean
import arch.cayenne.lib.http.HttpClient
import arch.cayenne.lib.http.HttpException
import arch.cayenne.lib.websocket.WebSocketManager
import com.walisport.module.business.common.data.constants.GameSortType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class GameRecentRepository(
    override val scope: CoroutineScope ,
    private val database: GameDatabase ,
    private val httpClient: HttpClient ,
    private val mockHttpClient: HttpClient ,
    private val socketManager: WebSocketManager ,
    private val preloadResultChange: MutableStateFlow<PreloadEnum> ,
    private val manager: UserDataManager ,
) : BaseRepository() {


    suspend fun queryGameList(
        page: Int,
        sortType: GameSortType,
        suppliers: List<Int>,
        category: Int
    ): ApiResponseState {
        val api = mockHttpClient.create(IGameRecentApi::class.java)
        return suspendCancellableCoroutine<ApiResponseState> { cancellableContinuation ->
            scope.launch(Dispatchers.IO) {
                mockHttpClient.safeRequest(
                    request = {
                        api.queryGameList(
                            page = page ,
                            pageSize = DEFAULT_GAME_SIZE ,
                            sort = sortType.type ,
                            supplier = suppliers ,
                            category = category
                        )
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

    //查询是否点击游戏详情页面
    suspend fun queryGameClick() : GameBean {
        return database.gameDao().queryGameBean(1)
    }

    //设置游戏点击状态
    suspend fun setGameClick(flag:Int){
        scope.launch(Dispatchers.IO) {
            database.gameDao().insert(GameBean(id = 1, clickFlag = flag))
        }
    }

    companion object {
        const val DEFAULT_GAME_SIZE = 10
        const val INITIAL_PAGE = 1
    }


}