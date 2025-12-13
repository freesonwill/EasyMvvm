package com.walisport.module.hall.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.loge
import arch.cayenne.lib.common.data.constants.PreloadEnum
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.database.GameDatabase
import arch.cayenne.lib.database.entity.GameSupplierDataModel
import arch.cayenne.lib.http.HttpClient
import arch.cayenne.lib.http.HttpException
import arch.cayenne.lib.websocket.WebSocketManager
import com.walisport.module.hall.data.constants.GameSortType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class HallRepository(
    override val scope: CoroutineScope ,
    private val database: GameDatabase ,
    private val httpClient: HttpClient ,
    private val mockHttpClient: HttpClient ,
    private val socketManager: WebSocketManager ,
    private val preloadResultChange: MutableStateFlow<PreloadEnum> ,
    private val manager: UserDataManager ,
) : BaseRepository() {
    private val _gameListLiveData: MutableLiveData<List<GameVo>> = MutableLiveData()
    val gameListLiveData: LiveData<List<GameVo>> = _gameListLiveData


    private val _gameCommonListLiveData: MutableLiveData<GameVo> = MutableLiveData()
    val gameCommonListLiveData: LiveData<GameVo> = _gameCommonListLiveData

    suspend fun queryGameList(
        page: Int ,
        sortType: GameSortType ,
        suppliers: List<Int> ,
        category: Int
    ): ApiResponseState {
        val api = mockHttpClient.create(IHallApi::class.java)
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

    //获取通用配置数据
    fun queryGameCommonList() {
        val api = mockHttpClient.create(IHallApi::class.java)
        scope.launch(Dispatchers.IO) {
            mockHttpClient.safeRequest(
                request = {
                    api.queryGameCommon()
                } ,
                onSuccess = { resp ->
                    if (resp.code == 0) {
                        "response------queryGameCommonList>${resp.code},${resp.data}".loge(TAG)
                        setSupplierList(resp.data.gameSupplier , resp.data.category)
                        // _gameCommonListLiveData.postValue(resp.data)
                    } else {
                        "response------queryGameCommonList>${resp.code},${resp.message}".loge(TAG)
                    }
                } ,
                onFailure = { code , msg , throwable ->
                    "response------queryGameCommonList>$code,$msg,$throwable".loge(TAG)
                }
            )
        }
    }


    //查询选中供应商数据
    suspend fun queryGameSuppliersSelect(gameType: Int) =
        database.supplierDao().querySelectSupplier(gameType)

    //监听供应商数据变化
    suspend fun observeSupplierByGameTypeId(gameType: Int) =
        database.supplierDao().observeSupplierGameTypeId(gameType)

    private suspend fun roomGameSupplier(
        supplier: List<GameSupplier> ,
        gameTypeList: List<GameCategoryVo>
    ): List<GameSupplierDataModel> {
        var list: MutableList<GameSupplierDataModel> = mutableListOf()
        gameTypeList.forEach { data ->//分类列表
            data.supplierIds.forEach { ids ->//分类列表 供应商列表ID
            supplier.forEach {it-> //供应商列表
                    if (ids == it.id) {
                        list.add(
                            GameSupplierDataModel(
                                index = list.size + 1 ,
                                id = it.id ,
                                hot = it.hot ,
                                icon = it.icon ,
                                gameTypeId = data.category ,
                                name = it.name ,
                                isSelected = 0
                            )
                        )
                    }
                }
            }
        }
        return list
    }

    //存储到room
    fun setSupplierList(supplier: List<GameSupplier> , gameTypeList: List<GameCategoryVo>) {
        scope.launch(Dispatchers.IO) {
            var list: List<GameSupplierDataModel> = roomGameSupplier(supplier , gameTypeList)
            database.supplierDao().insert(list)
        }
    }

    companion object {
        const val DEFAULT_GAME_SIZE = 10
        const val INITIAL_PAGE = 1
    }


}