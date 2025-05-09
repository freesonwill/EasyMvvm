package com.walisport.module.live.data.repository

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.database.GameDatabase
import arch.cayenne.lib.database.dao.MarketTypeBeanDao
import arch.cayenne.lib.database.entity.LiveSelectionBean
import arch.cayenne.lib.database.entity.MarketMenuBean
import arch.cayenne.lib.database.entity.MarketTypeBean
import com.walisport.module.live.LiveRemoteManager
import galaxy.common.proto.Common
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LiveBetOnRepository (private val database: GameDatabase, private val remoteManager: LiveRemoteManager
) : BaseRepository(){
    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    fun observeMarketTypeBean() = database.marketTypeDao().observeMarketTypeBean()
    fun queryLiveMarketType(matchId: Long) {
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
        }
    }

    private fun getMarketMenuBean(common: List<Common.MarketBase>): List<MarketMenuBean>{
        val marketBean = mutableListOf<MarketMenuBean>()
        common.forEach {
            marketBean.add(MarketMenuBean(marketId =it.marketId, marketName = it.marketName ))
        }
        return marketBean
    }
    suspend fun queryLiveMarketTypeAll() : List<MarketTypeBean> {
        return database.marketTypeDao().getAllMarketTypeBean()
    }
    suspend fun queryLiveSelectionBean(matchId: Long) : List<LiveSelectionBean> {
        return database.liveMatchDao().getSelectionsByIds(matchId)
    }


}