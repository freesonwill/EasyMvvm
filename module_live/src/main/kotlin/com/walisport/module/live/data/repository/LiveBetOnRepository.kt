package com.walisport.module.live.data.repository

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.database.dao.MarketTypeBeanDao
import arch.cayenne.lib.database.entity.MarketTypeBean
import com.walisport.module.live.LiveRemoteManager
import galaxy.common.proto.Common
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LiveBetOnRepository (private val marketTypeBeanDao: MarketTypeBeanDao, private val remoteManager: LiveRemoteManager
) : BaseRepository(){
    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    fun queryLiveMarketType(matchId: Long) {
        scope.launch {
            val resp = remoteManager.getMarketTypeReq(scope, matchId)
            marketTypeBeanDao.deleteAll()
            val data: MutableList<MarketTypeBean>  = mutableListOf()
            resp?.forEach {
                it.marketBaseList.forEach {marketType ->
                    data.add(MarketTypeBean(
                        code = it.code,
                        name = it.name,
                        marketId = marketType.marketId,
                        marketName = marketType.marketName
                    ))
                }
            }
            marketTypeBeanDao.insert(data)
        }
    }
}