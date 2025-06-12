package com.walisport.module.live.data.repository

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.database.GameDatabase
import arch.cayenne.lib.database.dao.MarketTypeBeanDao
import arch.cayenne.lib.database.entity.MarketMenuBean
import arch.cayenne.lib.database.entity.MarketTypeBean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LiveBetOnMenuRepository(private val database: GameDatabase) : BaseRepository() {
    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    suspend fun queryLiveMarketType(): List<MarketTypeBean> {
        return database.marketTypeDao().getAllMarketTypeBean()
    }

    suspend fun queryLiveMarketMenuByCode(): List<MarketMenuBean> {
        return database.marketTypeMenuDao().getAllMarketMenuAll()
    }
}