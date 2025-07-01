package com.walisport.module.live.data

import arch.cayenne.lib.database.entity.LiveMarketListBean
import arch.cayenne.lib.database.entity.LiveMarketSelectionBean
import arch.cayenne.lib.database.entity.LiveSelectionBean
import arch.cayenne.lib.database.entity.MarketMenuBean

data class LiveMatchListBeanFullData(
    val markets: List<LiveMarketListBean>
)

fun List<MarketMenuBean>.toData(selections:List<LiveSelectionBean>,code: String): LiveMatchListBeanFullData {
    val markets = mutableListOf<LiveMarketListBean>()
    this.forEach { menuBean ->
        val matchingSelections = selections
            .filter { it.marketId== menuBean.marketId }
            .map { selection ->
                LiveMarketSelectionBean(
                    selectionId = selection.selectionId,
                    name = selection.name,
                    shortName = selection.shortName,
                    odds = selection.odds,
                    active = selection.active,
                    parlay = selection.parlay,
                    marketId = selection.marketId,
                    marketName = selection.marketName,
                    style = selection.style,
                    oddsStatus = selection.oddsStatus,
                    code = code
                )
            }
            markets.add(
                LiveMarketListBean(
                    marketId = menuBean.marketId,
                    marketName = menuBean.marketName,
                    isSelect = menuBean.isSelect, // Default value
                    code = menuBean.code,
                    list = matchingSelections
                )
            )
    }
    return LiveMatchListBeanFullData(markets)

}