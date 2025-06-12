package com.walisport.module.live.data.repository

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.common.utils.ext.SportStringExt.toOdds
import arch.cayenne.lib.database.GameDatabase
import arch.cayenne.lib.database.dao.MarketTypeBeanDao
import arch.cayenne.lib.database.entity.LiveMatchBean
import arch.cayenne.lib.database.entity.LiveSelectionBean
import arch.cayenne.lib.database.entity.MarketMenuBean
import arch.cayenne.lib.database.entity.MarketTypeBean
import arch.cayenne.module.bet.data.BetInsertBean
import com.walisport.module.live.LiveRemoteManager
import com.xxx.qyplayer.log.extension.logTag
import galaxy.common.proto.Common
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LiveBetOnRepository (private val database: GameDatabase, private val remoteManager: LiveRemoteManager
) : BaseRepository(){
    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    fun observeMarketTypeBean() = database.marketTypeDao().observeMarketTypeBean()
    fun observeSelection(marketIds: List<Long>) = database.liveMatchDao().observeSelectionByIds(marketIds)
    //盘口列表
    fun queryLiveMarketType(matchId: Long,marketBeanCallback: (List<MarketTypeBean>) -> Unit,marketBeanMenCallback: (List<MarketMenuBean>) -> Unit) {
        scope.launch {
            val resp = remoteManager.getMarketTypeReq(scope, matchId)
            database.marketTypeDao().deleteAll()
            database.marketTypeMenuDao().deleteAll()
            val marketBean = mutableListOf<MarketTypeBean>()
            val marketBeanMenu = mutableListOf<MarketMenuBean>()
            resp?.forEach {
                marketBean.add(MarketTypeBean( code = it.code, name = it.name))
                marketBeanMenu.addAll(getMarketMenuBean(it.marketBaseList, it.code))
            }
            database.marketTypeDao().insert(marketBean)
            insertWithAutoIncrement(marketBeanMenu)
            scope.launch(Main){
                marketBeanMenCallback(marketBeanMenu)
                marketBeanCallback(marketBean)
            }
        }
    }

    suspend fun insertWithAutoIncrement(data: List<MarketMenuBean>): List<Long> {
        // 获取当前最大 number，默认为 0 如果表为空
        val maxOrderNumber =  database.marketTypeMenuDao().getMaxOrderNumber() ?: 0
        // 为每条记录设置递增的 number
        val updatedData = data.mapIndexed { index, bean ->
            bean.copy(number = maxOrderNumber + index + 1)
        }
        // 插入数据
        return  database.marketTypeMenuDao().insert(updatedData)
    }


    private fun getMarketMenuBean(common: List<Common.MarketBase>,code:String): List<MarketMenuBean>{
        val marketBean = mutableListOf<MarketMenuBean>()
        common.forEach {
            marketBean.add(MarketMenuBean(marketId =it.marketId, marketName = it.marketName ,code =  code))
        }
        return marketBean
    }

    suspend fun queryLiveSelectionBean(matchId: Long) : List<LiveSelectionBean> {
        return database.liveMatchDao().getSelectionsByIds(matchId)
    }

    suspend fun getSelectionInsertBean(matchId: Long, selectionId: Long): BetInsertBean? = withContext(scope.coroutineContext) {
        val match = database.liveMatchDao().getMatchById(matchId)
        val selectionBean = database.liveMatchDao().getSelectionBySelectionId(selectionId)
        matchSelectionInsertBean(match, selectionBean)
    }

private fun matchSelectionInsertBean(
        match: LiveMatchBean,
        selectionBean: LiveSelectionBean
    ): BetInsertBean? {
            return BetInsertBean(
                matchId = match.matchId,
                marketId = selectionBean.marketId,
                marketName = selectionBean.marketName,
                selectionId = selectionBean.selectionId,
                name = selectionBean.shortName,
                odds = selectionBean.odds.toOdds(),
                leagueName = match.basicInfo.tournamentName,
                matchName = match.basicInfo.matchName,
                isActive = selectionBean.active,
                isPlaying = match.basicInfo.status == 5,
                isParlay = selectionBean.parlay
            )
        return null
    }
}