package com.walisport.module.live.data.repository

import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.database.GameDatabase
import com.walisport.module.live.LiveRemoteManager
import com.walisport.module.live.data.model.LeagueMatchBean
import com.walisport.module.live.data.model.MatchBean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LiveLeagueRepository(
    private val remoteManager: LiveRemoteManager,
    private val database: GameDatabase,
    private val userManager: UserDataManager,
    ) : BaseRepository() {

    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    fun observeLoginChange() = database.sportLoginInfoDao().observerLogin()

    //获取联赛日程列表数据
    suspend fun getMatchLeagueList(
        leagueId: Int,
        cursorMatchId: Long,
        cursorMatchStartTime: Long
    ): ApiResponseState =
        withContext(scope.coroutineContext) {
            val result = remoteManager.getMatchLeagueListReq(scope, leagueId, cursorMatchId, cursorMatchStartTime)
            return@withContext if (result.error == null && result.data != null) {
                val matchList = ArrayList<MatchBean>()
                val strList = mutableSetOf<String>()
                result.data!!.matchList.mapIndexed { _, item ->
                    val timeStamp = item.basicInfo.startTime
                    val isToday = isTimeStampToday(timeStamp)
                    val temp = MatchBean(
                        matchId = item.matchId,
                        sportId = item.basicInfo.sportId,
                        homeLogo = item.basicInfo.homeTeamIcon,
                        isToday = isToday,
                        homeName = item.basicInfo.homeTeam,
                        awayLogo = item.basicInfo.awayTeamIcon,
                        awayName = item.basicInfo.awayTeam,
                        startTime = item.basicInfo.startTime,
                        itemColor = result.data!!.color
                    )
                    val date = convertStampToDate(timeStamp)
                    if (!strList.contains(date)) {
                        matchList.add(
                            MatchBean(
                                0, 0, true,
                                isToday = false,
                                weekDay = date,
                                homeLogo = "",
                                homeName = "",
                                awayLogo = "",
                                awayName = "",
                                startTime = 0L,
                                itemColor = result.data!!.color
                            )
                        )
                        strList.add(date)
                    }
                    matchList.add(temp)
                }
                val data = LeagueMatchBean(
                    match = matchList,
                    tournamentName = result.data!!.tournamentName,
                    tournamentShortName = result.data!!.tournamentShortName,
                    size = result.data!!.matchCount,
                    logo = result.data!!.icon,
                    color = result.data!!.color
                )
                ApiResponseState.Succeeded(data)
            } else {
                ApiResponseState.Failed(result.error)
            }
        }

    private fun convertStampToDate(timeStamp: Long): String {
        val date = Date(timeStamp)
        val format = SimpleDateFormat("MM月dd日 EEEE", Locale.getDefault())
        return format.format(date)
    }

    private fun isTimeStampToday(timeStamp: Long): Boolean {
        val format = SimpleDateFormat("MM-dd", Locale.getDefault())
        val date1 = Date(timeStamp)
        val day1 = format.format(date1)
        val date2 = Date(System.currentTimeMillis())
        val day2 = format.format(date2)
        return day1 == day2
    }
}