package com.walisport.module.setting.data

import arch.cayenne.lib.common.data.constants.SkinType
import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.common.data.constants.LanguageType
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.websocket.WebSocketManager
import arch.cayenne.lib.websocket.data.ApiCode
import arch.cayenne.lib.websocket.extension.sendAndWaitProtoMessageResponse
import galaxy.client.proto.Client
import galaxy.common.proto.Common
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingRepository(
    override val scope: CoroutineScope,
    private val socketManager: WebSocketManager,
    private val manager: UserDataManager
) : BaseRepository() {

    val observerOddsDisplay = manager.observe<Int>(UserDataKey.KEY_ODDS)

    //设置皮肤背景
    fun setSkinType(type: String) {
        manager.setKeyValue(UserDataKey.KEY_SKIN, type)
    }

    //获取皮肤背景
    fun getSkinType(): String {
        return manager.getValue(UserDataKey.KEY_SKIN, SkinType.DEFAULT)
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

    fun getSystemBet(): Boolean {
        return manager.getValue(UserDataKey.KEY_SYSTEM_BET, false)
    }

    fun getSystemFav(): Boolean {
        return manager.getValue(UserDataKey.KEY_SYSTEM_FAV, false)
    }

    fun getSystemAll(): Boolean {
        return manager.getValue(UserDataKey.KEY_SYSTEM_ALL, false)
    }

    //设置系统通知-开赛
    fun setKickGoal(bet: Boolean, fav: Boolean, all: Boolean) {
        manager.setKeyValue(UserDataKey.KEY_KICK_BET, bet)
        manager.setKeyValue(UserDataKey.KEY_KICK_FAV, fav)
        manager.setKeyValue(UserDataKey.KEY_KICK_ALL, all)
    }

    fun getKickBet(): Boolean {
        return manager.getValue(UserDataKey.KEY_KICK_BET, false)
    }

    fun getKickFav(): Boolean {
        return manager.getValue(UserDataKey.KEY_KICK_FAV, false)
    }

    fun getKickAll(): Boolean {
        return manager.getValue(UserDataKey.KEY_KICK_ALL, false)
    }

    //设置应用内通知-进球
    fun setAppGoal(bet: Boolean, fav: Boolean, all: Boolean) {
        manager.setKeyValue(UserDataKey.KEY_APP_BET, bet)
        manager.setKeyValue(UserDataKey.KEY_APP_FAV, fav)
        manager.setKeyValue(UserDataKey.KEY_APP_ALL, all)
    }

    fun getAppBet(): Boolean {
        return manager.getValue(UserDataKey.KEY_APP_BET, false)
    }

    fun getAppFav(): Boolean {
        return manager.getValue(UserDataKey.KEY_APP_FAV, false)
    }

    fun getAppAll(): Boolean {
        return manager.getValue(UserDataKey.KEY_APP_ALL, false)
    }

    fun updateSettingReq(setting: Common.Setting) {
        scope.launch(Dispatchers.IO) {
            socketManager.sendAndWaitProtoMessageResponse<Client.UpdateSettingResp>(
                scope = scope,
                dispatcher = Dispatchers.IO,
                apiCode = ApiCode.UPDATE_SYSTEM_SETTING,
            ) {
                Client.UpdateSettingReq.newBuilder().apply {
                    this.setting = setting
                }.build()
            }
        }
    }

    fun getOddsType(): OddsDisplayEnum {
        val value = manager.getValue(UserDataKey.KEY_ODDS, OddsDisplayEnum.EU.value)
        return OddsDisplayEnum.entries[value]
    }
}