package com.walisport.module.search.data.model

import com.walisport.module.search.data.constants.MatchStatusEnum
import galaxy.common.proto.Common
import java.io.Serializable

/** * 搜索结果中的比赛详细信息
 * @property matchId 比赛id
 * @property matchName 比赛名称
 * @property homeTeam 主队名称
 * @property homeTeamId 主队id
 * @property homeTeamIcon 主队icon
 * @property awayTeam 客队名称
 * @property awayTeamId 客队id
 * @property awayTeamIcon 客队icon
 * @property startTime 开始时间,13位时间戳
 * @property status 比赛状态 0-已结束 1-推迟 2-中断 3-取消 4-未开赛 5-进行中 6-延迟 7-废弃 8-暂停
 * @property tournamentId 联赛id
 * @property tournamentName 联赛名称
 * @property tournamentShortName 联赛简称
 * @property tournamentIcon 联赛icon
 * @property sportId 球类id
 * @property sportName 球类名称
 * @property liveInfo 比赛实时信息
 * @property betStop false: 未停止投注, true: 已停止投注
 * @property tournamentHot 是否热门联赛
 * @property tournamentWeight 联赛权重
 */
data class SearchMatchDetailBean(
    val matchId: Long,                          //比赛id
    val matchName: String,                      //比赛名称
    val homeTeam: String,                       //主队名称
    val homeTeamId: Int,                        //主队id
    val homeTeamIcon: String?,                  //主队icon
    val awayTeam: String,                       //客队名称
    val awayTeamId: Int,                        //客队id
    val awayTeamIcon: String?,                  //客队icon
    val startTime: Long,                        //开始时间,13位时间戳
    val status: MatchStatusEnum,                    //比赛状态 0-已结束 1-推迟 2-中断 3-取消 4-未开赛 5-进行中 6-延迟 7-废弃 8-暂停
    val tournamentId: Int,                      //联赛id
    val tournamentName: String,                 //联赛名称
    val tournamentShortName: String,            //联赛简称
    val tournamentIcon: String?,                //联赛icon
    val sportId: Int,                           //球类id
    val sportName: String,                      //球类名称
    val liveInfo: SearchMatchLiveInfoBean? = null,  //比赛实时信息
    val betStop: Boolean = false,               // false: 未停止投注, true: 已停止投注
    val tournamentHot: Boolean = false,         //是否热门联赛
    val tournamentWeight: Int = 0,              //联赛权重
): Serializable {
    companion object {
        fun from(resp: Common.MatchBasicInfo): SearchMatchDetailBean {
            return SearchMatchDetailBean(
                matchId = resp.matchId,
                matchName = resp.matchName,
                homeTeam = resp.homeTeam,
                homeTeamId = resp.homeTeamId,
                homeTeamIcon = resp.homeTeamIcon,
                awayTeam = resp.awayTeam,
                awayTeamId = resp.awayTeamId,
                awayTeamIcon = resp.awayTeamIcon,
                startTime = resp.startTime,
                status = MatchStatusEnum.fromCode(resp.status),
                tournamentId = resp.tournamentId,
                tournamentName = resp.tournamentName,
                tournamentShortName = resp.tournamentShortName,
                tournamentIcon = resp.tournamentIcon,
                sportId = resp.sportId,
                sportName = resp.sportName,
                liveInfo = if (resp.hasLiveInfo()) SearchMatchLiveInfoBean.from(resp.liveInfo) else null,
                betStop = resp.betStop,
                tournamentHot = resp.tournamentHot,
                tournamentWeight = resp.tournamentWeight
            )
        }
    }
}