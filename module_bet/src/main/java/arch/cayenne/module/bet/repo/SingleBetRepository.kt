package arch.cayenne.module.bet.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.common.utils.ext.StringExt.toValue
import arch.cayenne.lib.database.dao.BetDao
import arch.cayenne.lib.database.entity.BetBean
import arch.cayenne.lib.database.entity.BetTypeEnum
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SingleBetRepository(private val betDao: BetDao) : BaseRepository() {
    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    fun observeSingleBet() = betDao.observeSingleBet()

    fun removeBet(id: Int) {
        scope.launch {
            betDao.removeBet(id)
        }
    }

    fun saveToCombo(id: Int) {
        scope.launch {
            betDao.updateBetType(id, BetTypeEnum.COMBO)
        }
    }

    // TODO 此為測試用！！之後會刪除  此為測試用！！之後會刪除  此為測試用！！之後會刪除
    fun addMockData() {
        scope.launch {
            val data = betDao.getBetSheet()
            betDao.insert(
                BetBean(
                gameId = data.size,
                betTeamName = "Test ${data.size}",
                handicap = "-1.5",
                odds = "1.98".toValue(),
                betType = BetTypeEnum.COMBO,
                leagueName = "世界盃",
                matchName = "中國vs巴西"
            )
            )
        }
    }

    suspend fun getOneMockData() = withContext(scope.coroutineContext) {
        val data = betDao.getBetSheet()
        if (data.isEmpty()) {
            BetBean(
                gameId = 0,
                betTeamName = "Test ${0}",
                handicap = "-1.5",
                odds = "1.98".toValue(),
                betType = BetTypeEnum.SINGLE,
                leagueName = "世界盃",
                matchName = "中國vs巴西"
            ).apply {
                betDao.insert(this)
            }
        } else {
            null
        }
    }
}