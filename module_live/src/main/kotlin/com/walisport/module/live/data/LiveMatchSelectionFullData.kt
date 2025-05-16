package com.walisport.module.live.data

import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.common.utils.ext.SportStringExt.toOdds
import arch.cayenne.lib.database.entity.LiveMarketBean
import arch.cayenne.lib.database.entity.LiveMarketDetailBean
import arch.cayenne.lib.database.entity.LiveMatchBasicInfoBean
import arch.cayenne.lib.database.entity.LiveMatchBean
import arch.cayenne.lib.database.entity.LiveMatchLiveInfoBean
import arch.cayenne.lib.database.entity.LiveSelectionBean
import galaxy.common.proto.Common
import galaxy.common.proto.Common.Market

data class LiveMatchSelectionFullData(
    val selections: List<LiveSelectionBean>,
)

fun List<Market>.selectionsToRoomData(): LiveMatchSelectionFullData {
    val selections = mutableListOf<LiveSelectionBean>()
    this.forEach { market ->
        market.marketDetailList.forEachIndexed { index, detail ->
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
                        odds = selection.odds.toOdds(),
                        active = selection.active,
                        parlay = selection.parlay,
                        style = market.style
                    )
                )
            }
        }
    }
    return LiveMatchSelectionFullData(
        selections
    )
}