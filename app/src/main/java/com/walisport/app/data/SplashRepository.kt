package com.walisport.app.data

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.common.data.constants.SkinType
import arch.cayenne.lib.common.utils.helper.CountDownHelper
import arch.cayenne.lib.socket.WebSocketManager
import arch.cayenne.lib.socket.data.ConnectState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.first

class SplashRepository(
    override val scope: CoroutineScope,
    private val socketManager: WebSocketManager,
    private val userDataManager: UserDataManager
) : BaseRepository() {

    private val countDownHelper = CountDownHelper()

    internal var countDown: Int by countDownHelper::countDown
    internal val countDownSecondsLD: SharedFlow<Int> by countDownHelper::countDownSecondsLD
    internal val isCountDownStart by countDownHelper::isCountDownStart
    init {
        countDown = 5_000
    }

    fun saveUserData(uid: Int, token: String) {
        userDataManager.setKeyValue(UserDataKey.KEY_UID, uid)
        userDataManager.setKeyValue(UserDataKey.KEY_TOKEN, token)
    }

    //获取皮肤背景
    fun getSkinType(): String {
        return userDataManager.getStringValue(UserDataKey.KEY_SKIN, SkinType.SKIN_WHITE_BLUE.value)
    }

    suspend fun startSocket() : ConnectState {
        return socketManager.connect("wss://betwavepro.ja700.com/fb-ws").first()
    }
}