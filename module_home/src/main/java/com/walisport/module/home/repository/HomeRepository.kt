package com.walisport.module.home.repository

import com.google.protobuf.GeneratedMessageLite
import com.walisport.lib.base.data.repository.BaseRepository
import com.walisport.lib.database.GameDatabase
import com.walisport.lib.database.entity.SportBean
import com.walisport.lib_socket.WebSocketManager
import com.walisport.lib_socket.data.ApiCode
import com.walisport.lib_socket.data.SocketResponseData
import com.walisport.lib_socket.extension.sendAndWaitProtoMessageResponse
import com.walisport.module.home.data.PlayType
import galaxy.client.proto.Client
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class HomeRepository(
    override val scope: CoroutineScope,
    private val socketManager: WebSocketManager,
    private val database: GameDatabase
) : BaseRepository() {
    suspend fun getStatistical() : Boolean {
        val res = socketManager.sendAndWaitProtoMessageResponse<Client.StatisticalResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.STATISTICAL,
        ) {
            Client.StatisticalReq.newBuilder().build()
        }
        return if (res.error == null && res.data != null) {
            saveSports(res.data!!)
            true
        } else {
            false
        }
    }
    private fun saveSports(data: Client.StatisticalResp) {
        val beanMap = HashMap<Int, SportBean>()
        data.statisticalList.forEach { play ->
            play.sportStatisticalList.forEach { sport ->
                val bean = if (beanMap.containsKey(sport.sportId)) {
                    beanMap[sport.sportId]!!
                } else {
                    SportBean(sportId = sport.sportId, sportName = sport.sportName)
                }
                when(play.playType) {
                    PlayType.All.id -> bean.allMatchCount = sport.matchCount
                    PlayType.Today.id -> bean.todayMatchCount = sport.matchCount
                    PlayType.EarlyLines.id -> bean.earlyLinesMatchCount = sport.matchCount
                    PlayType.Champion.id -> bean.champion = sport.matchCount
                    PlayType.InPlayOdds.id -> bean.inPlayOdds = sport.matchCount
                }
                beanMap[sport.sportId] = bean
            }
            database.sportDao().insert(beanMap.map { it.value }.toList())
        }
    }
}