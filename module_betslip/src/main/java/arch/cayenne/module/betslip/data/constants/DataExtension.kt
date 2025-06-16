package arch.cayenne.module.betslip.data.constants

import arch.cayenne.module.betslip.data.model.BetSlipOrderBean
import arch.cayenne.module.betslip.data.model.EarlySettlePriceBean
import arch.cayenne.module.betslip.data.model.OrderSelectionBean
import arch.cayenne.module.betslip.data.model.ReserveOrderBean
import arch.cayenne.module.betslip.data.model.ReserveOrderSelectionBean
import galaxy.common.proto.Common

object DataExtension {

    fun Common.Order.toOrderBean(): BetSlipOrderBean {
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
            earlySettlePrice = earlySettlePrice.toEarlySettlePriceBean()
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
            matchBasic = matchBasic,
            status = status,
            endScore = endScore,
            inPlay = inPlay
        )
    }

    fun Common.EarlySettlePrice.toEarlySettlePriceBean(): EarlySettlePriceBean {
        return EarlySettlePriceBean(
            betId = betId,
            price = price,
            settleTotal = settleTotal,
            settleMin = settleMin,
            settleStatus = settleStatus
        )
    }

    fun Common.ReserveOrder.toReserveOrderBean(): ReserveOrderBean {
        return ReserveOrderBean(
            reserveId = reserveId,
            reserveTime = reserveTime,
            betAmount = betAmount,
            selection = selection,
            status = status
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
            matchBasic = matchBasic
        )
    }
}