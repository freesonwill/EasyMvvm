package com.walisport.module.popup.slot.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.data.remote.ApiResponseState.Start.dataAs
import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.loge
import arch.cayenne.lib.common.data.constants.PreloadEnum
import arch.cayenne.lib.common.data.manager.UserDataManager
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

class PopupSlotRepository(
    override val scope: CoroutineScope,
    private val database: GameDatabase,
    private val httpClient: HttpClient,
    private val socketManager: WebSocketManager,
    private val preloadResultChange: MutableStateFlow<PreloadEnum>,
    private val manager: UserDataManager,
) : BaseRepository() {

    private val _popupSlotDataListLiveData: MutableLiveData<List<PopupSlotBean>> =
        MutableLiveData()
    val popupSlotDataListLiveData: LiveData<List<PopupSlotBean>>
        get() = _popupSlotDataListLiveData

    private val _popUpShowLiveData: MutableLiveData<List<Boolean>> = MutableLiveData()
    val popUpShowLiveData: MutableLiveData<List<Boolean>>
        get() = _popUpShowLiveData


    var slot0Animated: Boolean = false
    var slot1Animated: Boolean = false


    private var hasFetched = false

    fun getPopupSlotData() {
        //该方法仅被执行一次
        if (hasFetched) return
        hasFetched = true
        scope.launch {
            val response = queryPopupList()

            if (response is ApiResponseState.Failed) {
            } else if (response is ApiResponseState.Succeeded<*>) {
                val popupVoList = response.dataAs<List<PopupVo>>()

                _popupSlotDataListLiveData.postValue(popupVoList?.map { popupVo ->
                    PopupSlotBean(
                        popupId = popupVo.popupId,
                        data = popupVo.items.map { itemVo ->
                            PopupSlotDataModel(
                                operateType = itemVo.operateType,
                                operateParams = itemVo.operateParams,
                                bottomImagePath = itemVo.bottomImagePath
                            )
                        }
                    )
                })
                _popUpShowLiveData.postValue(popupVoList?.map { true } ?: emptyList())

            }


        }
    }

    private suspend fun queryPopupList(
    ): ApiResponseState {
        val api = httpClient.create(IPopupApi::class.java)
        return suspendCancellableCoroutine<ApiResponseState> { cancellableContinuation ->
            scope.launch(Dispatchers.IO) {
                httpClient.safeRequest(
                    request = {
                        api.getPopupList()
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

    fun hidePopupSlot(id: Int) {
        scope.launch {
            val currentList = _popUpShowLiveData.value?.toMutableList() ?: return@launch
            if (id in currentList.indices) {
                currentList[id] = false
                _popUpShowLiveData.postValue(currentList)
            }
        }
    }

}


/**
 *
 * 小弹窗广告位数据模型
 * @date: 2025/12/9 14:29
 * @description:
 */
data class PopupSlotDataModel(
    val operateType: String,
    val operateParams: List<String>,
    val bottomImagePath: String
)

data class PopupSlotBean(
    val popupId: Long,
    val data: List<PopupSlotDataModel>,
)