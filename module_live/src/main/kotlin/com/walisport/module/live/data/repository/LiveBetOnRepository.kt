package com.walisport.module.live.data.repository

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.common.utils.ext.SportStringExt.toOdds
import arch.cayenne.lib.database.GameDatabase
import arch.cayenne.lib.database.dao.MarketTypeBeanDao
import arch.cayenne.lib.database.entity.LiveMatchBean
import arch.cayenne.lib.database.entity.LiveSelectionBean
import arch.cayenne.lib.database.entity.MarketMenuBean
import arch.cayenne.lib.database.entity.MarketTypeBean
import arch.cayenne.module.bet.data.BetInsertBean
import com.walisport.module.live.LiveRemoteManager
import galaxy.common.proto.Common
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LiveBetOnRepository (private val database: GameDatabase, private val remoteManager: LiveRemoteManager
) : BaseRepository(){
    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    fun observeMarketTypeBean() = database.marketTypeDao().observeMarketTypeBean()
    fun observeSelection(marketIds: List<Long>) = database.liveMatchDao().observeSelectionByIds(marketIds)
    fun queryLiveMarketType(matchId: Long,callback: (List<MarketTypeBean>) -> Unit) {
        scope.launch {
            val resp = remoteManager.getMarketTypeReq(scope, matchId)
            database.marketTypeDao().deleteAll()
            val data = resp?.mapIndexed { _, marketType ->
                MarketTypeBean(
                    code = marketType.code,
                    name = marketType.name,
                    marketMenuBean = getMarketMenuBean(marketType.marketBaseList)
                )
            } ?: emptyList()
            database.marketTypeDao().insert(data)
            scope.launch(Dispatchers.Main){
                callback(data)
            }
        }
    }

    private fun getMarketMenuBean(common: List<Common.MarketBase>): List<MarketMenuBean>{
        val marketBean = mutableListOf<MarketMenuBean>()
        common.forEach {
            marketBean.add(MarketMenuBean(marketId =it.marketId, marketName = it.marketName ))
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