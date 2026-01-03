package com.walisport.module.me.data

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.loge
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.database.GameDatabase
import arch.cayenne.lib.database.entity.AvatarEmbedded
import arch.cayenne.lib.database.entity.UserDataBean
import arch.cayenne.lib.database.entity.WalletBean
import arch.cayenne.lib.http.HttpClient
import arch.cayenne.lib.http._interface.IAccount
import arch.cayenne.lib.http.data.AccountInfo
import arch.cayenne.lib.websocket.WebSocketManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MeRepository(
    override val scope: CoroutineScope,
    private val socketManager: WebSocketManager,
    private val manager: UserDataManager,
    private val database: GameDatabase,
    private val httpClient: HttpClient
) : BaseRepository() {

    private val currencyConfigDao = database.currencyConfigDao()
    fun observeUserInfo() = database.userDataDao().observeUser()

    fun getAccountInfo() {
        val api = httpClient.create(IAccount::class.java)
        scope.launch(Dispatchers.IO) {
            httpClient.safeRequest(
                request = {
                    api.profileInfo()
                },
                onSuccess = { resp ->
                    "======${resp.data}".loge("测试")
                    if (resp.code == 0) {
                        launch {
                            saveAccountInfo(resp.data)
                        }
                    }
                },
                onFailure = { code, msg, throwable ->
                    "ProfileInfo failure, response------>$code,$msg,$throwable".loge(TAG)
                }
            )
        }
    }

    private suspend fun saveAccountInfo(profileInfo: AccountInfo) {
        database.userDataDao().insert(
            UserDataBean(
                nickname = profileInfo.nickname,
                avatar = AvatarEmbedded(
                    url = profileInfo.avatar.url,
                    thumbhash = profileInfo.avatar.thumbhash
                ),
                Uid = 100L,
                registerTime = profileInfo.registerTime,
                vipLevel = profileInfo.vipLevel,
                score = profileInfo.score,
                ccy = profileInfo.ccy,
                list = profileInfo.list.map { WalletBean(it.ccy, it.score, it.exchangeScore) },
                admittedBetScore = profileInfo.admittedBetScore,
                requiredAdmittedBetScore = profileInfo.requiredAdmittedBetScore,
                vipStage = profileInfo.vipStage,
                nicknameChangeCount = profileInfo.nicknameChangeCount,
            )
        )
        //更新余额信息
        database.infoDao().updateBalance(profileInfo.score)
    }

}