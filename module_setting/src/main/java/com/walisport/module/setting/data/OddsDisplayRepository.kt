package com.walisport.module.setting.data

import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.websocket.WebSocketManager
import arch.cayenne.lib.websocket.data.ApiCode
import arch.cayenne.lib.websocket.extension.sendAndWaitProtoMessageResponse
import galaxy.client.proto.Client
import galaxy.common.proto.Common
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OddsDisplayRepository(
    override val scope: CoroutineScope,
    private val manager: UserDataManager,
    private val socketManager: WebSocketManager
) : BaseRepository() {

    //设置赔率显示方式
    suspend fun setOddsType(type: OddsDisplayEnum) = withContext(scope.coroutineContext) {
        manager.setKeyValue(UserDataKey.KEY_ODDS, type.value)
        val resp = socketManager.sendAndWaitProtoMessageResponse<Client.UpdateSettingResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.UPDATE_SYSTEM_SETTING,
        ) {
            Client.UpdateSettingReq.newBuilder().apply {
                this.setting = Common.Setting.newBuilder().apply {
                    this.setOddType(type.value)
                }.build()
            }.build()
        }
        if (resp.error == null && resp.data != null) {
            ApiResponseState.Succeeded(resp.data)
        } else {
            ApiResponseState.Failed(error = resp.error)
        }
    }

    //获取赔率显示方式
    fun getOddsType(): OddsDisplayEnum {
        val value = manager.getValue(UserDataKey.KEY_ODDS, OddsDisplayEnum.EU.value)
        return OddsDisplayEnum.entries[value]
    }
}