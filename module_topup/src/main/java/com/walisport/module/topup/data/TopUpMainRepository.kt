package com.walisport.module.topup.data

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.database.dao.CoinDao
import arch.cayenne.lib.database.entity.CoinBean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class TopUpMainRepository(private val coinDao: CoinDao) : BaseRepository() {

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
        val list = listOf(
            CoinBean(2, true, isSelect = false, name = "USDT", icon = "", unit = ""),
            CoinBean(3, true, isSelect = false, "BTC", "", ""),
            CoinBean(4, true, isSelect = false, "ETH", "", "")
        )
        coinDao.insertCurrencyList(list)
    }
}

