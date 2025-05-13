package com.walisport.module.live.data.repository

import arch.cayenne.lib.base.data.repository.BaseRepository
import com.walisport.module.live.LiveRemoteManager
import com.walisport.module.live.data.model.LeagueMatchBean
import com.walisport.module.live.data.model.MatchBean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LiveLeagueRepository(
    private val remoteManager: LiveRemoteManager
) : BaseRepository() {

    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    //获取联赛日程列表数据
    suspend fun getMatchLeagueData(leagueId: Int, page: Int): LeagueMatchBean? {
        val resp = remoteManager.getMatchLeagueReq(scope, leagueId, page)
        val list = ArrayList<MatchBean>()
        val stringSet = mutableSetOf<String>()
        resp?.matchList?.mapIndexed { _, item ->
            val temp = MatchBean(
                matchId = item.matchId,
                sportId = item.basicInfo.sportId,
                homeLogo = item.basicInfo.homeTeamIcon,
                homeName = item.basicInfo.homeTeam,
                awayLogo = item.basicInfo.awayTeamIcon,
                awayName = item.basicInfo.awayTeam,
                startTime = item.basicInfo.startTime
            )
            val date = convertStampToDate(item.basicInfo.startTime)
            if (!stringSet.contains(date)) {
                list.add(MatchBean(0, 0, true, date, "", "", "", "", 0L))
                stringSet.add(date)
            }
            list.add(temp)
        }
        val data = resp?.let {
            LeagueMatchBean(
                match = list,
                tournamentName = resp.tournamentName,
                tournamentShortName = resp.tournamentShortName,
                logo = resp.icon,
                color = resp.color
            )
        }
        return data
    }

    private fun convertStampToDate(timeStamp: Long): String {
        val date = Date(timeStamp)
        val format = SimpleDateFormat("MM月dd日 EEEE", Locale.getDefault())
        return format.format(date)
    }
}