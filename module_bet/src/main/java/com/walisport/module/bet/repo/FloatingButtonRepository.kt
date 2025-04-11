package com.walisport.module.bet.repo

import com.walisport.lib.base.data.repository.BaseRepository
import com.walisport.lib.database.dao.BetDao
import com.walisport.lib.database.entity.BetTypeEnum
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class FloatingButtonRepository(private val betDao: BetDao): BaseRepository() {

    fun observeComboBetCount() = betDao.observeComboBetCount()

    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    suspend fun getSingleBetId() = with(scope.coroutineContext) {
        betDao.getBetSheet().first().gameId
    }

    fun saveToSingleBet(id: Int) {
        scope.launch {
            betDao.updateBetType(id, BetTypeEnum.SINGLE)
        }
    }
}