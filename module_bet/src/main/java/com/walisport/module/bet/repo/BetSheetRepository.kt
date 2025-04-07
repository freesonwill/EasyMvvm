package com.walisport.module.bet.repo

import com.walisport.lib.base.data.repository.BaseRepository
import com.walisport.lib.database.dao.BetDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class BetSheetRepository(private val betDao: BetDao) : BaseRepository() {
    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    fun observeBetSheet() = betDao.observeBetSheet()

    fun sendBetting() {

    }
}