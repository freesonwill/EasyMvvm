package com.walisport.module.live.data

import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.common.utils.ext.SportIntExt.getOdds
import arch.cayenne.lib.common.utils.ext.SportStringExt.toOdds
import arch.cayenne.lib.database.entity.LiveMarketDetailBean
import arch.cayenne.lib.database.entity.LiveSelectionBean
import arch.cayenne.lib.database.entity.LiveSelectionBeanRecord
import com.walisport.module.live.data.LiveOddsStatusEnum
import com.walisport.module.live.data.constants.LiveMatchBetStatus
import com.xxx.qyplayer.log.extension.logTag
import galaxy.common.proto.Common.Market
import kotlin.collections.mutableListOf

data class LiveMatchSelectionFullData(
    val selections: List<LiveSelectionBean>,
    val selectionsRecord: List<LiveSelectionBeanRecord>,
    val selectionsDelete: List<Long>,
)

fun List<Market>.selectionsToRoomData(rec: List<LiveSelectionBeanRecord>): LiveMatchSelectionFullData {
    val selections = mutableListOf<LiveSelectionBean>()
    val selectionsRecord = mutableListOf<LiveSelectionBeanRecord>()
    val selectionsDelete = mutableListOf<Long>()
    this.forEach { market ->
        market.marketDetailList.forEachIndexed { index, detail ->
            detail.selectionList.filter { it.selectionId != 0L }.forEach { selection ->
                if (market.status==LiveMatchBetStatus.DELETE.code){
                    LogUtils.e("selectionsToRoomData---delete--name${selection.name},${selection.name}-----")
                    selectionsDelete.add(selection.selectionId)
                } else if  (market.status==LiveMatchBetStatus.DEFAULT.code){
                   // status为0时不用做处理，因为用的相同数据结构，查询比赛列表或比赛详情时，market的status会是0
                }else{//新增,修改
                    var data = rec.find { it.selectionId == selection.selectionId }
                    var status = if(data==null){ LiveOddsStatusEnum.SAME.status} else if (data.odds.toDouble() < selection.odds.toDouble()) {
                        LiveOddsStatusEnum.UP.status
                    } else if (data.odds.toDouble() >selection.odds.toDouble()) {
                        LiveOddsStatusEnum.DOWN.status
                    } else {
                        LiveOddsStatusEnum.SAME.status
                    }
                    LogUtils.e("selectionsToRoomData-----name${selection.name}------oddsRecord=${data?.odds?.toDouble()}-----odds${selection?.odds?.toDouble()}-----status =${status}")
                    selectionsRecord.add(
                        LiveSelectionBeanRecord(
                            marketId = market.marketId,
                            odds = selection.odds,
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
                            odds = selection.odds,
                            active = selection.active,
                            parlay = selection.parlay,
                            style = market.style,
                            oddsStatus = status
                        )
                    )
                }
            }
        }
    }
    return LiveMatchSelectionFullData(
        selections, selectionsRecord, selectionsDelete
    )
}