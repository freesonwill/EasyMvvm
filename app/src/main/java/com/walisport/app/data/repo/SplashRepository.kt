package com.walisport.app.data.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.common.data.constants.LanguageType
import arch.cayenne.lib.common.data.constants.SkinType
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.common.utils.helper.CountDownHelper
import arch.cayenne.lib.websocket.WebSocketManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow

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
        countDown = 2_000
    }

    fun saveUserData(uid: Int, token: String) {
        userDataManager.setKeyValue(UserDataKey.KEY_UID, uid)
        userDataManager.setKeyValue(UserDataKey.KEY_TOKEN, token)
    }

    //获取皮肤背景
    fun getSkinType(): String {
        return userDataManager.getValue(UserDataKey.KEY_SKIN, SkinType.DEFAULT)
    }

    //获取语言类型
    fun getLanguageType(): String {
        return userDataManager.getValue(UserDataKey.KEY_LANGUAGE, LanguageType.LANGUAGE_SIMPLE.value)
    }
}