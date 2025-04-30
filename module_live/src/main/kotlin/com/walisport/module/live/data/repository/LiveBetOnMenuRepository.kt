package com.walisport.module.live.data.repository

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.database.dao.MarketTypeBeanDao
import arch.cayenne.lib.database.entity.MarketTypeBean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class LiveBetOnMenuRepository (private val marketTypeBeanDao: MarketTypeBeanDao) : BaseRepository(){
    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    suspend fun queryLiveMarketType() :List<MarketTypeBean>{
            return marketTypeBeanDao.getAllMarketTypeBean()
    }

    suspend fun updateMarketIdByMarketSelect(marketId: Long, marketSelect: Boolean, selectId: Long = 0, isSelect: Boolean = false):Int{
       return marketTypeBeanDao.updateMarketIdByMarketSelect( marketId,marketSelect,selectId,isSelect)
    }


}