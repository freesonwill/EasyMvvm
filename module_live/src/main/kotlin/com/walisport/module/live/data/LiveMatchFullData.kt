package com.walisport.module.live.data

import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.database.entity.LiveMarketBean
import arch.cayenne.lib.database.entity.LiveMarketDetailBean
import arch.cayenne.lib.database.entity.LiveMatchBasicInfoBean
import arch.cayenne.lib.database.entity.LiveMatchBean
import arch.cayenne.lib.database.entity.LiveMatchLiveInfoBean
import arch.cayenne.lib.database.entity.LiveSelectionBean
import arch.cayenne.lib.database.entity.LiveSelectionBeanRecord
import galaxy.common.proto.Common

data class LiveMatchFullData(
    val match: List<LiveMatchBean>,
    val markets: List<LiveMarketBean>,
    val selections: List<LiveSelectionBean>,
    val selectionsRecord: List<LiveSelectionBeanRecord>,
)

fun Common.Match.toRoomData(): LiveMatchFullData {
    val matches = mutableListOf<LiveMatchBean>()
    val markets = mutableListOf<LiveMarketBean>()
    val selections = mutableListOf<LiveSelectionBean>()
    val selectionsRecord = mutableListOf<LiveSelectionBeanRecord>()
    matches.add(
        LiveMatchBean(
            matchId = this.matchId,
            collect = this.collect,
            basicInfo = LiveMatchBasicInfoBean(
                matchId = this.basicInfo.matchId,
                matchName = this.basicInfo.matchName,
                homeTeam = this.basicInfo.homeTeam,
                homeTeamId = this.basicInfo.homeTeamId,
                homeTeamIcon = this.basicInfo.homeTeamIcon,
                homeHistoryVs = this.basicInfo.homeHistoryVsList.joinToString(separator = ","),
                awayTeam = this.basicInfo.awayTeam,
                awayTeamId = this.basicInfo.awayTeamId,
                awayTeamIcon = this.basicInfo.awayTeamIcon,
                awayHistoryVs = this.basicInfo.awayHistoryVsList.joinToString(separator = ","),
                startTime = this.basicInfo.startTime,
                status = this.basicInfo.status,
                tournamentId = this.basicInfo.tournamentId,
                tournamentName = this.basicInfo.tournamentName,
                tournamentShortName = this.basicInfo.tournamentShortName,
                tournamentIcon = this.basicInfo.tournamentIcon,
                sportId = this.basicInfo.sportId,
                sportName = this.basicInfo.sportName,
                betStop = this.basicInfo.betStop,
                tournamentHot = this.basicInfo.tournamentHot,
                tournamentWeight = this.basicInfo.tournamentWeight
            ),
            liveInfo = LiveMatchLiveInfoBean(
                clock = this.basicInfo.liveInfo.clock,
                rollClock = this.basicInfo.liveInfo.rollClock,
                period = this.basicInfo.liveInfo.period,
                score = this.basicInfo.liveInfo.score,
                liveVideo = this.basicInfo.liveInfo.liveVideo,
                charRoom = this.basicInfo.liveInfo.chatRoom,
                viewerCount = this.basicInfo.liveInfo.viewerCount,
                clockModified = this.basicInfo.liveInfo.clockModified
            )
        )
    )
    this.marketList.forEach { market ->
        LogUtils.e("matchMainMatchResult-------marketList--------id${market.marketId}--name${market.marketName}")
        markets.add(
            LiveMarketBean(
                marketId = market.marketId,
                marketName = market.marketName,
                status = market.status
            )
        )

        var selectionCount = 0
        market.marketDetailList.forEachIndexed { index, detail ->
            selectionCount += detail.selectionList.size
            detail.selectionList.filter { it.selectionId != 0L }.forEach { selection ->
                selections.add(
                    LiveSelectionBean(
                        marketId = market.marketId,
                        marketName =market.marketName,
                        selectionId = selection.selectionId,
                        detail = LiveMarketDetailBean(
                            detailId = index,
                            specifier = detail.specifier,
                            active = detail.active,
                            parlay = detail.parlay,
                        ),
                        name = selection.name,
                        shortName = selection.shortName,
                        odds = selection.odds,
                        active = selection.active,
                        parlay = selection.parlay,
                        style = market.style,
                        oddsStatus = LiveOddsStatusEnum.SAME.status
                    )
                )
                selectionsRecord.add(LiveSelectionBeanRecord(
                    marketId = market.marketId,
                    selectionId = selection.selectionId,
                    odds = selection.odds,
                ))

            }
        }
    }
    return LiveMatchFullData(
        matches,
        markets,
        selections,
        selectionsRecord,
    )

}