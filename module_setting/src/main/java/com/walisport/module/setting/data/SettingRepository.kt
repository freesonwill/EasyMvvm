package com.walisport.module.setting.data

import arch.cayenne.lib.common.data.constants.SkinType
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
import org.koin.java.KoinJavaComponent.inject

class SettingRepository(
    override val scope: CoroutineScope,
    private val socketManager: WebSocketManager,
) : BaseRepository() {

    private val manager: UserDataManager by inject(UserDataManager::class.java)

    //设置皮肤背景
    fun setSkinType(type: String) {
        manager.setKeyValue(UserDataKey.KEY_SKIN, type)
    }

    //获取皮肤背景
    fun getSkinType(): String {
        return manager.getValue(UserDataKey.KEY_SKIN, SkinType.DEFAULT)
    }

    //设置赔率显示方式
    fun setOddsType(type: Int) {
        manager.setKeyValue(UserDataKey.KEY_ODDS, type)
    }

    //获取赔率显示方式
    fun getOddsType(): Int {
        return manager.getValue(UserDataKey.KEY_ODDS, 0)
    }

    //设置语言类型
    fun setLanguageType(type: String) {
        manager.setKeyValue(UserDataKey.KEY_LANGUAGE, type)
    }

    //获取语言类型
    fun getLanguageType(): String {
        return manager.getValue(UserDataKey.KEY_LANGUAGE, LanguageType.LANGUAGE_SIMPLE.value)
    }

    //设置系统通知-进球
    fun setSystemGoal(bet: Boolean, fav: Boolean, all: Boolean) {
        manager.setKeyValue(UserDataKey.KEY_SYSTEM_BET, bet)
        manager.setKeyValue(UserDataKey.KEY_SYSTEM_FAV, fav)
        manager.setKeyValue(UserDataKey.KEY_SYSTEM_ALL, all)
    }

    //设置系统通知-开赛
    fun setKickGoal(bet: Boolean, fav: Boolean, all: Boolean) {
        manager.setKeyValue(UserDataKey.KEY_KICK_BET, bet)
        manager.setKeyValue(UserDataKey.KEY_KICK_FAV, fav)
        manager.setKeyValue(UserDataKey.KEY_KICK_ALL, all)
    }

    //设置应用内通知-进球
    fun setAppGoal(bet: Boolean, fav: Boolean, all: Boolean) {
        manager.setKeyValue(UserDataKey.KEY_APP_BET, bet)
        manager.setKeyValue(UserDataKey.KEY_APP_FAV, fav)
        manager.setKeyValue(UserDataKey.KEY_APP_ALL, all)
    }

    //修改系统配置
    suspend fun updateSettingReq(setting: Common.Setting): Boolean? {
        val res = socketManager.sendAndWaitProtoMessageResponse<Client.UpdateSettingResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.UPDATE_SYSTEM_SETTING,
        ) {
            Client.UpdateSettingReq.newBuilder().apply {
                this.setting = setting
            }.build()
        }
        if (res.error == null && res.data != null) {
            return res.data!!.success
        }
        return null
    }
}