package com.walisport.module.hall.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.loge
import arch.cayenne.lib.common.data.constants.PreloadEnum
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.database.GameDatabase
import arch.cayenne.lib.database.dao.GameSupplierDao
import arch.cayenne.lib.database.entity.GameSupplierDataModel
import arch.cayenne.lib.http.HttpClient
import arch.cayenne.lib.websocket.WebSocketManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow

class HallRepository(
    override val scope: CoroutineScope,
    private val database: GameDatabase,
    private val httpClient: HttpClient,
    private val mockHttpClient: HttpClient,
    private val socketManager: WebSocketManager,
    private val preloadResultChange: MutableStateFlow<PreloadEnum>,
    private val manager: UserDataManager,
) : BaseRepository() {
    private val _gameListLiveData: MutableLiveData<List<GameVo>> = MutableLiveData()
    val gameListLiveData: LiveData<List<GameVo>> = _gameListLiveData


    private val _gameCommonListLiveData: MutableLiveData<GameVo> = MutableLiveData()
    val gameCommonListLiveData: LiveData<GameVo> = _gameCommonListLiveData

    fun queryGameList(page: Int) {
        val api = mockHttpClient.create(IHallApi::class.java)
        scope.launch(Dispatchers.IO) {
            mockHttpClient.safeRequest(
                request = {
                    api.queryGameList(page, 10)
                },
                onSuccess = { resp ->
                    if (resp.code == 0) {
                        _gameListLiveData.postValue(resp.data.list)
                    } else {
                        "response------>${resp.code},${resp.message}".loge(TAG)
                    }
                },
                onFailure = { code, msg, throwable ->
                    "response------>$code,$msg,$throwable".loge(TAG)
                }
            )
        }
    }

    //获取通用配置数据
    fun queryGameCommonList() {
        val api = mockHttpClient.create(IHallApi::class.java)
        scope.launch(Dispatchers.IO) {
            mockHttpClient.safeRequest(
                request = {
                    api.queryGameCommon()
                },
                onSuccess = { resp ->
                    if (resp.code == 0) {
                        "response------queryGameCommonList>${resp.code},${resp.data}".loge(TAG)
                        setSupplierList(resp.data.gameSupplier, resp.data.category)
                       // _gameCommonListLiveData.postValue(resp.data)
                    } else {
                        "response------queryGameCommonList>${resp.code},${resp.message}".loge(TAG)
                    }
                },
                onFailure = { code, msg, throwable ->
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
        supplier: List<GameSupplier>,
        gameTypeList: List<GameCategoryVo>
    ): List<GameSupplierDataModel> {
        var list: MutableList<GameSupplierDataModel> = mutableListOf()
        gameTypeList.forEach { data ->
            supplier.forEach {
                data.supplierIds.forEach { ids ->
                    if (ids == it.id) {
                        list.add(
                            GameSupplierDataModel(
                                index = list.size + 1,
                                id = it.id,
                                hot = it.hot,
                                icon = it.icon,
                                gameTypeId = ids,
                                name = it.name,
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
    fun setSupplierList(supplier: List<GameSupplier>, gameTypeList: List<GameCategoryVo>) {
        scope.launch(Dispatchers.IO) {
            var list: List<GameSupplierDataModel> = roomGameSupplier(supplier, gameTypeList)
            database.supplierDao().insert(list)
        }
    }


}