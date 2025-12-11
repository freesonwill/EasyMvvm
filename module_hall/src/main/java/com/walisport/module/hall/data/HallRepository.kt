package com.walisport.module.hall.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.loge
import arch.cayenne.lib.common.data.constants.PreloadEnum
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.database.GameDatabase
import arch.cayenne.lib.http.HttpClient
import arch.cayenne.lib.websocket.WebSocketManager
import com.walisport.module.hall.data.constants.GameSortType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

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

    fun queryGameList(page: Int , sortType: GameSortType) {
        val api = mockHttpClient.create(IHallApi::class.java)
        scope.launch(Dispatchers.IO) {
            mockHttpClient.safeRequest(
                request = {
                    api.queryGameList(page , 10 , sort = sortType.desc)
                } ,
                onSuccess = { resp ->
                    if (resp.code == 0) {
                        if (page == 0) {
                            _gameListLiveData.value?.toMutableList()?.clear()
                        }

                        val newList = _gameListLiveData.value?.toMutableList() ?: mutableListOf()
                        newList.addAll(resp.data.list)
                        _gameListLiveData.postValue(newList)
                    } else {
                        "response------>${resp.code},${resp.message}".loge(TAG)
                    }
                } ,
                onFailure = { code , msg , throwable ->
                    "response------>$code,$msg,$throwable".loge(TAG)
                }
            )
        }
    }


}