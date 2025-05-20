package com.walisport.module.live.data

import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.common.utils.ext.SportStringExt.toOdds
import arch.cayenne.lib.database.entity.LiveMarketDetailBean
import arch.cayenne.lib.database.entity.LiveSelectionBean
import arch.cayenne.lib.database.entity.LiveSelectionBeanRecord
import com.xxx.qyplayer.log.extension.logTag
import galaxy.common.proto.Common.Market

data class LiveMatchSelectionFullData(
    val selections: List<LiveSelectionBean>,
    val selectionsRecord: List<LiveSelectionBeanRecord>,
)

fun List<Market>.selectionsToRoomData(rec: List<LiveSelectionBeanRecord>): LiveMatchSelectionFullData {
    val selections = mutableListOf<LiveSelectionBean>()
    val selectionsRecord = mutableListOf<LiveSelectionBeanRecord>()
    this.forEach { market ->
        market.marketDetailList.forEachIndexed { index, detail ->
            detail.selectionList.filter { it.selectionId != 0L }.forEach { selection ->
                var data = rec.find { it.selectionId == selection.selectionId }
                LogUtils.e("selectionsToRoomData-----oddsRecord=${data?.odds}-----odds${selection?.odds?.toOdds()}")
                var status = if(data==null){ LiveOddsStatusEnum.SAME.status} else if (data.odds > selection.odds.toOdds()) {
                    LiveOddsStatusEnum.DOWN.status
                } else if (data.odds <selection.odds.toOdds()) {
                    LiveOddsStatusEnum.UP.status
                } else {
                    LiveOddsStatusEnum.SAME.status
                }
                selectionsRecord.add(
                    LiveSelectionBeanRecord(
                        marketId = market.marketId,
                        odds = selection.odds.toOdds(),
                        selectionId = selection.selectionId
                    )
                )
                selections.add(
                    LiveSelectionBean(
                        marketId = market.marketId,
                        marketName = market.marketName,
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
                        style = market.style,
                        oddsStatus = status
                    )
                )
            }
        }
    }
    return LiveMatchSelectionFullData(
        selections, selectionsRecord
    )
}