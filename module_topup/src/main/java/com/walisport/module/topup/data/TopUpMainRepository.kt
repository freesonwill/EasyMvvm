package com.walisport.module.topup.data

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.database.dao.CoinDao
import arch.cayenne.lib.database.entity.CoinBean
import com.walisport.module.topup.TopUpRemoteManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class TopUpMainRepository(
    private val remote: TopUpRemoteManager,
    private val coinDao: CoinDao) : BaseRepository() {

    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    private val coinFlow = MutableSharedFlow<List<CoinBean>>()
    fun observeCurrency(): Flow<List<CoinBean>> = coinFlow

    init {
        scope.launch {
            launch {
                coinDao.observeCurrency().collect { data ->
                    if (data.isNotEmpty()) {
                        coinFlow.emit(data)
                    }
                }
            }
        }
    }

    suspend fun getCurrencyList() {
        val data = remote.getCurrencyTypeList()
        if (data.isNotEmpty()) {
            coinDao.insertCurrencyList(data)
        }
    }
}

