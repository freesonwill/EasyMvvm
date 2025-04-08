package com.walisport.module.bet.repo

import com.walisport.lib.base.data.repository.BaseRepository
import com.walisport.lib.database.dao.BetDao
import com.walisport.lib.database.entity.BetBean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BetSheetRepository(private val betDao: BetDao) : BaseRepository() {
    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    init {
        scope.launch {
            val data = betDao.getBetSheet()
            if (data.isEmpty()) {
                betDao.insert(BetBean(
                    gameId = 0,
                    betTeamName = "中國",
                    handicap = "-1.5",
                    odds = 1.98f,
                    status = 0,
                    leagueName = "世界盃",
                    matchName = "中國vs巴西"
                ))
            }
        }
    }
    fun observeBetSheet() = betDao.observeBetSheet()

    fun sendBetting() {

    }
}