package com.walisport.module.live.data.repository

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.database.dao.MarketTypeBeanDao
import arch.cayenne.lib.database.entity.MarketTypeBean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LiveBetOnMenuRepository(private val marketTypeBeanDao: MarketTypeBeanDao) : BaseRepository() {
    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    suspend fun queryLiveMarketType(): List<MarketTypeBean> {
        return marketTypeBeanDao.getAllMarketTypeBean()
    }
}