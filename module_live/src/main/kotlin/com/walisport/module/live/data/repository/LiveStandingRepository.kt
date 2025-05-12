package com.walisport.module.live.data.repository

import arch.cayenne.lib.base.data.repository.BaseRepository
import com.walisport.module.live.LiveRemoteManager
import com.walisport.module.live.data.model.StandingsBean
import com.walisport.module.live.data.model.TeamBean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class LiveStandingRepository(
    private val remoteManager: LiveRemoteManager
) : BaseRepository() {

    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    //获取积分榜实时数据
    suspend fun getCompetitionReq(compId: Int): List<StandingsBean> {
        val resp = remoteManager.getCompetitionReq(scope, compId)
        val data = resp?.tablesList?.mapIndexed { index, competitionTable ->
            val rows = competitionTable.rowsList?.mapIndexed { idx, item ->
                TeamBean(
                    idx,
                    item.teamName,
                    item.teamLogo,
                    item.total,
                    item.won,
                    item.draw,
                    item.loss,
                    item.goals,
                    item.goalsAgainst,
                    item.points
                )
            } ?: emptyList()
            StandingsBean(
                id = index,
                conference = competitionTable.conference,
                group = competitionTable.group,
                stage = competitionTable.stageId,
                rows = rows
            )
        } ?: emptyList()
        return data
    }
}