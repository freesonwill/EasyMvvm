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
import galaxy.client.proto.Client
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
}