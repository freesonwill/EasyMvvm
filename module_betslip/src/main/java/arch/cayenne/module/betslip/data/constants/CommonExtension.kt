package arch.cayenne.module.betslip.data.constants

import arch.cayenne.lib.database.entity.BetSlipOrderBean
import arch.cayenne.lib.database.entity.BetSlipReserveBean
import arch.cayenne.lib.database.entity.EarlySettlePriceBean
import arch.cayenne.lib.database.entity.MatchBasicInfoBean
import arch.cayenne.lib.database.entity.MatchLiveInfoBean
import arch.cayenne.lib.database.entity.OrderSelectionBean
import arch.cayenne.lib.database.entity.ReserveOrderSelectionBean
import galaxy.common.proto.Common

object CommonExtension {

    fun Common.Order.toOrderBean(betSlipType: Int): BetSlipOrderBean {
        return BetSlipOrderBean(
            betId = betId,
            betTime = betTime,
            settleTime = settleTime,
            betAmount = betAmount,
            returnAmount = returnAmount,
            selectionsList = selectionsList.map { it.toOrderSelectionBean() },
            comboType = comboType,
            comboK = comboK,
            comboV = comboV,
            comboCount = comboCount,
            odds = odds,
            status = status,
            earlySupport = earlySupport,
            earlyBetAmount = earlyBetAmount,
            earlyReturnAmount = earlyReturnAmount,
            earlySettleTimes = earlyCount,
            resultStatus = resultStatus,
            earlySettlePrice = earlySettlePrice.toEarlySettlePriceBean(),
            betSlipType = betSlipType
        )
    }

    fun Common.OrderSelection.toOrderSelectionBean(): OrderSelectionBean {
        return OrderSelectionBean(
            selectionId = selectionId,
            selectionName = selectionName,
            odds = odds,
            marketName = marketName,
            marketId = marketId,
            specifier = specifier,
            betScore = betScore,
            matchBasic = matchBasic.toMatchBasicInfoBean(),
            status = status,
            endScore = endScore,
            inPlay = inPlay
        )
    }

    fun Common.EarlySettlePrice.toEarlySettlePriceBean(): EarlySettlePriceBean {
        return EarlySettlePriceBean(
            price = price,
            settleTotal = settleTotal,
            settleMin = settleMin,
            settleStatus = settleStatus
        )
    }

    fun Common.ReserveOrder.toReserveOrderBean(): BetSlipReserveBean {
        return BetSlipReserveBean(
            reserveId = reserveId,
            reserveTime = reserveTime,
            betAmount = betAmount,
            selection = selection.toReserveOrderSelectionBean(),
            betStatus = status
        )
    }

    fun Common.ReserveOrderSelection.toReserveOrderSelectionBean(): ReserveOrderSelectionBean {
        return ReserveOrderSelectionBean(
            selectionId = selectionId,
            selectionName = selectionName,
            odds = odds,
            marketName = marketName,
            marketId = marketId,
            specifier = specifier,
            matchBasic = matchBasic.toMatchBasicInfoBean(),
            liveInfo = matchBasic.liveInfo.toMatchLiveInfoBean()
        )
    }

    fun Common.MatchBasicInfo.toMatchBasicInfoBean(): MatchBasicInfoBean {
        return MatchBasicInfoBean(
            matchId = matchId,
            matchName = matchName,
            homeTeam = homeTeam,
            homeTeamId = homeTeamId,
            homeTeamIcon = homeTeamIcon,
            awayTeam = awayTeam,
            awayTeamId = awayTeamId,
            awayTeamIcon = awayTeamIcon,
            startTime = startTime,
            status = status,
            tournamentId = tournamentId,
            tournamentName = tournamentName,
            tournamentShortName = tournamentShortName,
            tournamentIcon = tournamentIcon,
            sportId = sportId,
            sportName = sportName,
            betStop = betStop,
            tournamentHot = tournamentHot,
            tournamentWeight = tournamentWeight
        )
    }

    fun Common.MatchLiveInfo.toMatchLiveInfoBean(): MatchLiveInfoBean {
        return MatchLiveInfoBean(
            clock = clock,
            rollClock = rollClock,
            period = periodName,
            score = score,
            liveVideo = liveVideo,
            charRoom = chatRoom,
            viewerCount = viewerCount,
            clockModified = clockModified,
            homeScore = homeScore,
            awayScore = awayScore
        )
    }
}