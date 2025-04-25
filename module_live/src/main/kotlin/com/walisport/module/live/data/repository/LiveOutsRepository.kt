package com.walisport.module.live.data.repository

import arch.cayenne.lib.base.data.repository.BaseRepository
import com.walisport.module.live.LiveRemoteManager
import com.walisport.module.live.data.model.Incidents
import com.walisport.module.live.data.model.MatchTrendData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class LiveOutsRepository(
    private val remoteManager: LiveRemoteManager
) : BaseRepository() {

    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    //获取比赛趋势实时数据
    suspend fun getMatchTrendReq(matchId: Long): MatchTrendData {
        val resp = remoteManager.getMatchTrendReq(scope, matchId)
        val event = resp?.incidentsList?.mapIndexed { index, item ->
            Incidents(
                time = item.time,
                position = item.position,
                type = item.type
            )
        } ?: emptyList()
        val list = ArrayList<Int>()
        resp?.dataList?.mapIndexed { index, item ->
            list.addAll(item.valuesList)
        } ?: emptyList()
        return MatchTrendData(event, list)
    }
}