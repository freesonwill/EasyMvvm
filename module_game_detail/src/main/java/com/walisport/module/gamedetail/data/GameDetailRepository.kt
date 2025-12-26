package com.walisport.module.gamedetail.data

import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.loge
import arch.cayenne.lib.common.data.constants.PreloadEnum
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.common.utils.ext.ccyToSymbol
import arch.cayenne.lib.common.utils.ext.symbolUrl
import arch.cayenne.lib.database.GameDatabase
import arch.cayenne.lib.database.entity.CurrencyBean
import arch.cayenne.lib.http.HttpClient
import arch.cayenne.lib.http.HttpException
import arch.cayenne.lib.websocket.WebSocketManager
import com.walisport.module.business.common.data.IGameFavouriteApi
import com.walisport.module.business.common.data.ProfileCollectEditVo
import com.walisport.module.gamedetail.data.model.CurrencyInfoBean
import com.walisport.module.gamedetail.data.model.GameDetailBean
import com.walisport.module.gamedetail.data.model.GameDetailVo
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

    private val currencyConfigDao = database.currencyConfigDao()

    suspend fun getGameDetail(id: Long): ApiResponseState {
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

    suspend fun toGameDetailBean(vo: GameDetailVo?): GameDetailBean {

        return GameDetailBean(
            id = vo?.gameType?.toLong() ?: 0L ,
            name = vo?.name.orEmpty() ,
            type = vo?.category ?: 0 ,
            supplier = vo?.supplier.orEmpty() ,
            avatar = vo?.icon.orEmpty() ,
            reward = (vo?.reward ?: 0.0f).toDouble() ,
            maxOdds = vo?.maxOdds ?: 0 ,
            online = vo?.online ?: 0 ,
            score = vo?.score ?: 0.0 ,
            comments = vo?.comments ?: 0 ,
            tryIt = vo?.tryIt ?: false ,
            hasMore = vo?.hasMore ?: false ,
            collect = vo?.collect ?: false ,
            materials = vo?.materials.orEmpty() ,
            currency = vo?.ccyList?.mapNotNull {
                currencyConfigDao.getCurrencyByCcy(it)?.let { bean -> toCurrencyInfoBean(bean) }
            } ?: emptyList())
    }

    private fun toCurrencyInfoBean(currencyBean: CurrencyBean): CurrencyInfoBean {
        return CurrencyInfoBean(
            id = currencyBean.id ,
            isVirtual = currencyBean.virtual ,
            rate = currencyBean.rate ,
            unit = currencyBean.unit ,
            name = currencyBean.name ,

            )
    }

    suspend fun updateGameCollect(gameId: Long , collect: Boolean): ApiResponseState {
        val api = mockHttpClient.create(IGameFavouriteApi::class.java)
        return suspendCancellableCoroutine<ApiResponseState> { cancellableContinuation ->
            scope.launch(Dispatchers.IO) {
                mockHttpClient.safeRequest(
                    request = {
                        api.updateGameCollect(
                            body = ProfileCollectEditVo(
                                gameType = gameId.toInt() ,
                                collect = collect
                            )
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


    companion object {
        const val DEFAULT_PAGE_SIZE = 10
        const val INITIAL_PAGE = 1
    }


}