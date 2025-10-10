package com.walisport.module.setting.data

import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.data.constants.LanguageType
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.database.dao.BetDao
import arch.cayenne.lib.skin.LanguageManager
import arch.cayenne.lib.websocket.WebSocketManager
import arch.cayenne.lib.websocket.data.ApiCode
import arch.cayenne.lib.websocket.extension.sendAndWaitProtoMessageResponse
import galaxy.client.proto.Client
import galaxy.common.proto.Common
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

class LanguageRepository(
    override val scope: CoroutineScope,
    private val manager: UserDataManager,
    private val socketManager: WebSocketManager,
    private val languageManager: LanguageManager,
    private val betDao: BetDao
) : BaseRepository() {

    fun observeComboBetCount() = betDao.observeComboCount()

    //设置语言类型
    fun getLanguageType(): LanguageType {
        val lang = manager.getValue(UserDataKey.KEY_LANGUAGE, LanguageType.LANGUAGE_SIMPLE.value)
        return LanguageType.findLanguage(lang)
    }

    private suspend fun setLanguageType(type: LanguageType) {
        manager.setKeyValue(UserDataKey.KEY_LANGUAGE, type.value)
        languageManager.changeLanguage(Locale(type.value))
    }


    suspend fun saveLanguageType(type: LanguageType) = withContext(scope.coroutineContext) {
        val resp = socketManager.sendAndWaitProtoMessageResponse<Client.UpdateSettingResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.UPDATE_SYSTEM_SETTING,
        ) {
            Client.UpdateSettingReq.newBuilder().apply {
                this.setting = getSystemSetting(type)
            }.build()
        }
        if (resp.error == null && resp.data != null) {
            setLanguageType(type)
            ApiResponseState.Succeeded(resp.data)
        } else {
            ApiResponseState.Failed(error = resp.error)
        }
    }

    private fun getSystemSetting(type: LanguageType): Common.Setting {
        return Common.Setting.newBuilder().apply {
            lang = when (type) {
                LanguageType.LANGUAGE_ENGLISH -> "en-US"
                LanguageType.LANGUAGE_ID -> "id-ID"
                LanguageType.LANGUAGE_PT -> "pt-PT"
                else -> "zh-CN"
            }
        }.build()
    }

}