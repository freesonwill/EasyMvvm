package arch.cayenne.module.home.data

import arch.cayenne.lib.base.utils.LogUtilsExt.logi
import arch.cayenne.lib.database.entity.MarketBean
import arch.cayenne.lib.database.entity.MarketDetailBean
import arch.cayenne.lib.database.entity.MatchBasicInfoBean
import arch.cayenne.lib.database.entity.MatchBean
import arch.cayenne.lib.database.entity.MatchLiveInfoBean
import arch.cayenne.lib.database.entity.SelectionBean
import galaxy.common.proto.Common


//TODO 暫時放置的資料結構，待更新
data class Match(
    val matchId: Long,
    val collect: Boolean,
    val basicInfo: MatchBasicInfo,
    val market: List<Market>,
)

data class MatchBasicInfo(
    val matchId: Long,
    val matchName: String,
    val homeTeam: String,
    val homeTeamId: Int,
    val homeTeamIcon: String,
    val awayTeam: String,
    val awayTeamId: Int,
    val awayTeamIcon: String,
    val startTime: Long,
    val status: Int, //比赛状态 0-已结束 1-推迟 2-中断 3-取消 4-未开赛 5-进行中 6-延迟 7-废弃 8-暂停
    val tournamentId: Int,
    val tournamentName: String,
    val tournamentShortName: String,
    val tournamentIcon: String,
    val sportId: Int,
    val sportName: String,
    val liveInfo: MatchLiveInfo,
    val betStop: Boolean,// false: 未停止投注, true: 已停止投注
    val tournamentHot: Boolean,
    val tournamentWeight: Int,
)
data class MatchLiveInfo(
    val clock: Int,//走表时间，以秒为单位
    val rollClock: Boolean,//是否走表
    val period: String,//阶段
    val score: String,//比分
    val liveVideo: Boolean,//该比赛是否有视频或者直播
    val charRoom: Boolean,//是否开启了聊天室
    val viewerCount: Int,//观看数量
    val clockModified: Long,//走表修改时间
)
data class Market(
    val marketId: Long,
    val marketName: String,
    val marketDetail: List<MarketDetail>,
    val status: Int,//0-默认 1-新增 2-修改 3-删除 (仅在推送时使用)
)
data class MarketDetail(
    val specifier: String,//盘口说明符，可能为空
    val selection: List<Selection>,
    val active: Boolean,//true - 可以投注  false - 不可投注
    val parlay: Boolean,//true - 支持串关  false - 不支持串关
)
data class Selection(
    val selectionId: Long,
    val name: String,
    val shortName: String,
    val odds: String,
    val active: Boolean,
    val parlay: Boolean,
)

data class MatchFullData(
    val match: List<MatchBean>,
    val markets: List<MarketBean>,
    val details: List<MarketDetailBean>,
    val selections: List<SelectionBean>
)

fun List<Common.Match>.toRoomData(): MatchFullData {
    val matches = mutableListOf<MatchBean>()
    val markets = mutableListOf<MarketBean>()
    val details = mutableListOf<MarketDetailBean>()
    val selections = mutableListOf<SelectionBean>()
    for(match in this) {
        matches.add(
            MatchBean(
                matchId = match.matchId,
                collect = match.collect,
                basicInfo = MatchBasicInfoBean(
                    matchId = match.basicInfo.matchId,
                    matchName = match.basicInfo.matchName,
                    homeTeam = match.basicInfo.homeTeam,
                    homeTeamId = match.basicInfo.homeTeamId,
                    homeTeamIcon = match.basicInfo.homeTeamIcon,
                    awayTeam = match.basicInfo.awayTeam,
                    awayTeamId = match.basicInfo.awayTeamId,
                    awayTeamIcon = match.basicInfo.awayTeamIcon,
                    startTime = match.basicInfo.startTime,
                    status = match.basicInfo.status,
                    tournamentId = match.basicInfo.tournamentId,
                    tournamentName = match.basicInfo.tournamentName,
                    tournamentShortName = match.basicInfo.tournamentShortName,
                    tournamentIcon = match.basicInfo.tournamentIcon,
                    sportId = match.basicInfo.sportId,
                    sportName = match.basicInfo.sportName,
                    betStop = match.basicInfo.betStop,
                    tournamentHot = match.basicInfo.tournamentHot,
                    tournamentWeight = match.basicInfo.tournamentWeight
                ),
                liveInfo = MatchLiveInfoBean(
                    clock = match.basicInfo.liveInfo.clock,
                    rollClock = match.basicInfo.liveInfo.rollClock,
                    period = match.basicInfo.liveInfo.period,
                    score = match.basicInfo.liveInfo.score,
                    liveVideo = match.basicInfo.liveInfo.liveVideo,
                    charRoom = match.basicInfo.liveInfo.chatRoom,
                    viewerCount = match.basicInfo.liveInfo.viewerCount,
                    clockModified = match.basicInfo.liveInfo.clockModified
                )
            )
        )

        for (market in match.marketList) {
            markets.add(
                MarketBean(
                    marketId = market.marketId,
                    matchId = match.matchId,
                    marketName = market.marketName,
                    status = market.status
                )
            )

            market.marketDetailList.forEachIndexed { index, detail ->
                "KC_ detailId = ${market.marketId * 100 + index}".logi("KC_KC_")
                details.add(
                    MarketDetailBean(
                        detailId = market.marketId * 100 + index,
                        marketId = market.marketId,
                        specifier = detail.specifier,
                        active = detail.active,
                        parlay = detail.parlay
                    )
                )

                for (selection in detail.selectionList) {
                    selections.add(
                        SelectionBean(
                            selectionId = selection.selectionId,
                            detailId = market.marketId * 100 + index,
                            name = selection.name,
                            shortName = selection.shortName,
                            odds = selection.odds,
                            active = selection.active,
                            parlay = selection.parlay
                        )
                    )
                }
            }
        }
    }


    return MatchFullData(
        match = matches,
        markets = markets,
        details = details,
        selections = selections
    )
}