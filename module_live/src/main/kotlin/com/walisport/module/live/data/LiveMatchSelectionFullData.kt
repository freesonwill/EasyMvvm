package com.walisport.module.live.data

import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.common.utils.ext.SportIntExt.getOdds
import arch.cayenne.lib.common.utils.ext.SportStringExt.toOdds
import arch.cayenne.lib.database.entity.LiveMarketBean
import arch.cayenne.lib.database.entity.LiveMarketDetailBean
import arch.cayenne.lib.database.entity.LiveSelectionBean
import arch.cayenne.lib.database.entity.LiveSelectionBeanRecord
import com.walisport.module.live.data.LiveOddsStatusEnum
import com.walisport.module.live.data.constants.LiveMatchBetStatus
import com.xxx.qyplayer.log.extension.logTag
import galaxy.common.proto.Common.Market
import kotlin.collections.mutableListOf
import kotlin.math.min

data class LiveMatchSelectionFullData(
    val selectionsEdit: List<LiveSelectionBean>,//修改投注区
    val selectionsEditId : List<Long>,//记录变化的注区ID,用于显示上升,下降
    val selectionsRecord: List<LiveSelectionBeanRecord>,//投注区记录
    val selectionsDelete: List<Long>,//删除的注区
    val marketsDelete: List<Long>,//删除的盘口
    val selectionsAdd: List<LiveSelectionBean>,//新增注区
    val marketsAdd: List<LiveMarketBean>////新增盘口
)

fun List<Market>.selectionsToRoomData(rec: List<LiveSelectionBeanRecord>): LiveMatchSelectionFullData {
    val data: List<Market> = this
    val selectionsEdit = mutableListOf<LiveSelectionBean>()//修改投注区
    val selectionsEditId = mutableListOf<Long>()//记录变化的注区ID,用于显示上升,下降
    val selectionsRecord = mutableListOf<LiveSelectionBeanRecord>()//投注区记录
    val selectionsDelete = mutableListOf<Long>()//删除的注区
    val marketsDelete = mutableListOf<Long>()//删除的盘口
    val selectionsAdd = mutableListOf<LiveSelectionBean>()//新增注区
    val marketsAdd = mutableListOf<LiveMarketBean>()//新增盘口
    data.forEach { market ->
        market.marketDetailList.forEachIndexed { index, detail ->
            if (LiveMatchBetStatus.ADD.code == market.status) {
                marketsAdd.add(
                    LiveMarketBean(
                        marketId = market.marketId,
                        marketName = market.marketName,
                        status = market.status
                    )
                )
            } else if (LiveMatchBetStatus.DELETE.code == market.status) {
                marketsDelete.add(market.marketId)
            }
            detail.selectionList.filter { it.selectionId != 0L }.forEach { selection ->
                when (market.status) {
                    LiveMatchBetStatus.DELETE.code -> { // 删除
                        LogUtils.e("selectionsToRoomData---delete--name${selection.name},${selection.name}-----")
                        selectionsDelete.add(selection.selectionId)
                    }

                    LiveMatchBetStatus.MODIFY.code -> { // 修改
                        var data = rec.find { it.selectionId == selection.selectionId }
                        var status: Int = LiveOddsStatusEnum.SAME.status
                        if (data == null) {
                            status = LiveOddsStatusEnum.SAME.status
                        } else if (data.odds.toDouble() < selection.odds.toDouble()) {
                            status = LiveOddsStatusEnum.UP.status
                            selectionsEditId.add(selection.selectionId)
                        } else if (data.odds.toDouble() > selection.odds.toDouble()) {
                            status = LiveOddsStatusEnum.DOWN.status
                            selectionsEditId.add(selection.selectionId)
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
                        selectionsEdit.add(
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

                    LiveMatchBetStatus.ADD.code -> {// 新增
                        selectionsAdd.add(
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
                                oddsStatus = LiveOddsStatusEnum.SAME.status
                            )
                        )
                    }

                    else -> {// 默认
                        // status为0时不用做处理，因为用的相同数据结构，查询比赛列表或比赛详情时，market的status会是0
                    }


                }


            }
        }
    }
    return LiveMatchSelectionFullData(
         selectionsEdit,
     selectionsEditId ,
     selectionsRecord,
     selectionsDelete,
     marketsDelete,
     selectionsAdd,
     marketsAdd,
    )
}