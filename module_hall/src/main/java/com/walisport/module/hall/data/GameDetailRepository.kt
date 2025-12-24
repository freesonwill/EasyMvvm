package com.walisport.module.hall.data

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

    private val currencyConfigDao = database.currencyConfigDao()

    suspend fun recordBetting(
        page: Int ,
    ): ApiResponseState {
        val api = mockHttpClient.create(IRankingApi::class.java)
        return suspendCancellableCoroutine<ApiResponseState> { cancellableContinuation ->
            scope.launch(Dispatchers.IO) {
                mockHttpClient.safeRequest(
                    request = {
                        api.recordBetting(
                            page = page ,
                            pageSize = DEFAULT_PAGE_SIZE ,
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

    suspend fun recordBig(
        page: Int ,
    ): ApiResponseState {
        val api = mockHttpClient.create(IRankingApi::class.java)
        return suspendCancellableCoroutine<ApiResponseState> { cancellableContinuation ->
            scope.launch(Dispatchers.IO) {
                mockHttpClient.safeRequest(
                    request = {
                        api.recordBig(
                            page = page ,
                            pageSize = DEFAULT_PAGE_SIZE ,
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

    /**
     * 获取每日比赛
     */
    suspend fun getDailyMatch(page: Int , pageSize: Int = DEFAULT_PAGE_SIZE): ApiResponseState {
        val api = mockHttpClient.create(IRankingApi::class.java)
        return suspendCancellableCoroutine<ApiResponseState> { cancellableContinuation ->
            scope.launch(Dispatchers.IO) {
                mockHttpClient.safeRequest(
                    request = {
                        api.getDailyMatch(
                            page = page ,
                            pageSize = pageSize ,
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

    /**
     * 获取每日投注比赛信息
     */
    suspend fun getDayMatchDetail(): ApiResponseState {
        val api = mockHttpClient.create(IRankingApi::class.java)
        return suspendCancellableCoroutine<ApiResponseState> { cancellableContinuation ->
            scope.launch(Dispatchers.IO) {
                mockHttpClient.safeRequest(
                    request = {
                        api.dayMatchDetail()
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

    suspend fun toGameAllRankingListData(bettingVo: BettingVo): GameAllRankingListData {
        val currencyBean = currencyConfigDao.getCurrencyByCcy(bettingVo.ccy)
        return GameAllRankingListData(
            gameIconUrl = bettingVo.avatar.url ,
            gameName = bettingVo.name ,
            multiple = bettingVo.multiple ,
            symbol = currencyBean?.unit ?: bettingVo.ccy.ccyToSymbol() ,
            icon = currencyBean?.icon ?: bettingVo.ccy.symbolUrl() ,
            result = bettingVo.bonus.toFloat(),
            virtual = currencyBean?.virtual ?: false
        )
    }

    //BigVo转换为GameAllRankingListData
    suspend fun toGameAllRankingListData(bigVo: BigVo): GameAllRankingListData {
        val currencyBean = currencyConfigDao.getCurrencyByCcy(bigVo.ccy)
        return GameAllRankingListData(
            gameIconUrl = bigVo.avatar.url ,
            gameName = bigVo.name ,
            multiple = bigVo.multiple ,
            symbol = currencyBean?.unit ?: bigVo.ccy.ccyToSymbol() ,
            icon = currencyBean?.icon ?: bigVo.ccy.symbolUrl() ,
            result = bigVo.bonus.toFloat(),
            virtual = currencyBean?.virtual ?: false
        )
    }

    //DayVo转换为GameAllRankingTodayData
    suspend fun toGameAllRankingToday(dayVo: DayVo): GameAllRankingToday.GameAllRankingTodayData {
        val currencyBean = currencyConfigDao.getCurrencyByCcy(dayVo.ccy)
        return GameAllRankingToday.GameAllRankingTodayData(
            rank = dayVo.ranking ,
            playerName = dayVo.name ,
            symbol = currencyBean?.unit ?: dayVo.ccy.ccyToSymbol() ,
            betting = dayVo.bet.toDouble() ,
            bonus = dayVo.bonus.toDouble() ,
            myself = dayVo.mySelf
        )
    }


    companion object {
        const val DEFAULT_PAGE_SIZE = 10
        const val INITIAL_PAGE = 1
    }


}