package com.walisport.module.bet.repo

import com.walisport.lib.base.data.repository.BaseRepository
import com.walisport.lib.database.dao.BetDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ComboBetRepository(private val betDao: BetDao) : BaseRepository() {
    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    fun observeComboBet() = betDao.observeComboBet()

    fun removeBet(id: Int) {
        scope.launch {
            betDao.removeBet(id)
        }
    }

    fun removeAll() {
        scope.launch {
            betDao.deleteAll()
        }
    }

    fun saveToSingleBet(id: Int) {
        scope.launch {
            betDao.getBetById(id)?.let {
                it.status = 0
                betDao.update(it)
            }
        }
    }
}