package com.walisport.app.data.repo

import arch.cayenne.lib.common.data.constants.SkinType
import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.database.dao.SportDao
import arch.cayenne.lib.database.entity.ShowType
import arch.cayenne.lib.database.entity.SportBean
import arch.cayenne.lib.websocket.WebSocketManager
import arch.cayenne.lib.websocket.data.ApiCode
import arch.cayenne.lib.websocket.extension.sendAndWaitProtoMessageResponse
import com.walisport.module.setting.data.LanguageType
import galaxy.client.proto.Client
import galaxy.common.proto.Common
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @author: zhangsan
 * @date: 2025/3/14 16:58
 * @description:
 */
class MainRepository(
    override val scope: CoroutineScope,
    private val userDataManager: UserDataManager,
    private val socketManager: WebSocketManager,
    private val sportDao: SportDao
) : BaseRepository() {

    //获取皮肤背景
    fun getSkinType(): String {
        return userDataManager.getValue(UserDataKey.KEY_SKIN, SkinType.SKIN_WHITE_BLUE.value)
    }

    fun loadSportList() {
        scope.launch(Dispatchers.IO) {
            val result = socketManager.sendAndWaitProtoMessageResponse<Client.ListSportResp>(
                scope = scope,
                dispatcher = Dispatchers.IO,
                apiCode = ApiCode.LIST_SPORT
            ) {
                Client.ListSportReq.newBuilder().build()
            }
            if (result.error == null && result.data != null) {
                val data = result.data!!.sportList.map {
                    SportBean(
                        sportId = it.sportId,
                        sportName = it.sportName,
                        matchCount = 0,
                        sportOrder = 0,
                        type = ShowType.ALL
                    )
                }
                sportDao.insert(data)
            }
        }
    }

    private fun getNotifyMatchType(type: Int): Common.NotifyMatchType {
        return Common.NotifyMatchType.newBuilder().apply {
            when (type) {
                MATCH_GOAL -> {
                    betMatch = getSystemBet()
                    collectMatch = getSystemFav()
                    allMatch = getSystemAll()
                }

                MATCH_KICK -> {
                    betMatch = getKickBet()
                    collectMatch = getKickFav()
                    allMatch = getKickAll()
                }

                else -> {
                    betMatch = getAppBet()
                    collectMatch = getAppFav()
                    allMatch = getAppAll()
                }
            }
        }.build()
    }

    //上传本地三种配置信息：语言、赔率显示、通知(主题背景随设备不随账号)
    fun updateSettingReq() {
        scope.launch(Dispatchers.IO) {
            val language = getLanguageType()
            val oddsType = getOddsType()
            val sysGoal = getNotifyMatchType(MATCH_GOAL)
            val sysKick = getNotifyMatchType(MATCH_KICK)
            val app = getNotifyMatchType(MATCH_APP)
            val setting = Common.Setting.newBuilder().apply {
                lang = language          //语言类型
                oddType = oddsType       //赔率类型
                systemGoal = sysGoal     //系统通知-进球
                systemKickOff = sysKick  //系统通知-开球
                appGoal = app            //app内通知-开球
            }.build()
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

    private fun getLanguageType(): String {
        return userDataManager.getValue(UserDataKey.KEY_LANGUAGE, LanguageType.LANGUAGE_SIMPLE.value)
    }

    private fun getOddsType(): Int {
        return userDataManager.getValue(UserDataKey.KEY_ODDS, 0)
    }

    private fun getSystemBet(): Boolean {
        return userDataManager.getValue(UserDataKey.KEY_SYSTEM_BET, false)
    }

    private fun getSystemFav(): Boolean {
        return userDataManager.getValue(UserDataKey.KEY_SYSTEM_FAV, false)
    }

    private fun getSystemAll(): Boolean {
        return userDataManager.getValue(UserDataKey.KEY_SYSTEM_ALL, false)
    }

    private fun getKickBet(): Boolean {
        return userDataManager.getValue(UserDataKey.KEY_KICK_BET, false)
    }

    private fun getKickFav(): Boolean {
        return userDataManager.getValue(UserDataKey.KEY_KICK_FAV, false)
    }

    private fun getKickAll(): Boolean {
        return userDataManager.getValue(UserDataKey.KEY_KICK_ALL, false)
    }

    private fun getAppBet(): Boolean {
        return userDataManager.getValue(UserDataKey.KEY_APP_BET, false)
    }

    private fun getAppFav(): Boolean {
        return userDataManager.getValue(UserDataKey.KEY_APP_FAV, false)
    }

    private fun getAppAll(): Boolean {
        return userDataManager.getValue(UserDataKey.KEY_APP_ALL, false)
    }

    companion object {
        const val MATCH_GOAL = 1
        const val MATCH_KICK = 2
        const val MATCH_APP = 3
    }
}