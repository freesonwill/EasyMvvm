package com.walisport.module.setting.data

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.websocket.WebSocketManager
import kotlinx.coroutines.CoroutineScope

class OddsDisplayRepository(
    override val scope: CoroutineScope,
    private val manager: UserDataManager,
    private val socketManager: WebSocketManager,
) : BaseRepository() {

    //设置赔率显示方式
    fun setOddsType(type: Int) {
        manager.setKeyValue(UserDataKey.KEY_ODDS, type)
        //TODO API
    }

    //获取赔率显示方式
    fun getOddsType(): Int {
        return manager.getValue(UserDataKey.KEY_ODDS, OddsDisplayEnum.EU.value)
    }
}