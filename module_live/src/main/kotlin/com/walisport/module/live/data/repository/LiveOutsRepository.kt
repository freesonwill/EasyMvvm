package com.walisport.module.live.data.repository

import arch.cayenne.lib.base.data.repository.BaseRepository
import com.walisport.module.live.LiveRemoteManager
import com.walisport.module.live.data.model.Incidents
import com.walisport.module.live.data.model.MatchHalfTeamStats
import com.walisport.module.live.data.model.MatchLiveData
import com.walisport.module.live.data.model.MatchTrendData
import com.walisport.module.live.data.model.Stat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class LiveOutsRepository(
    private val remoteManager: LiveRemoteManager
) : BaseRepository() {

    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    //获取比赛趋势数据
    suspend fun getMatchTrendReq(matchId: Long): MatchTrendData {
        val resp = remoteManager.getMatchTrendReq(scope, matchId)
        val event = resp?.incidentsList?.mapIndexed { _, item ->
            Incidents(
                time = item.time,
                position = item.position,
                type = item.type
            )
        } ?: emptyList()
        val list = ArrayList<Int>()
        resp?.dataList?.mapIndexed { _, item ->
            list.addAll(item.valuesList)
        } ?: emptyList()
        return MatchTrendData(event, list)
    }

    //获取比赛统计数据
    suspend fun getMatchStatisticReq(matchId: Long): MatchLiveData {
        val resp = remoteManager.getMatchStatisticReq(scope, matchId)
        val teams = resp?.teamStatsList?.mapIndexed { _, item ->
            MatchHalfTeamStats(
                type = item.type,
                homeNum = item.homeNum,
                awayNum = item.awayNum
            )
        } ?: emptyList()
        val stats = resp?.statsList?.mapIndexed { _, item ->
            Stat(
                type = item.type,
                home = item.home,
                away = item.away
            )
        } ?: emptyList()
        return MatchLiveData(0, teams, stats)
    }
}