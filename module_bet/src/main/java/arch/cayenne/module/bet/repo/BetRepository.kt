package arch.cayenne.module.bet.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.database.dao.BetDao
import arch.cayenne.lib.database.entity.BetBean
import arch.cayenne.lib.database.entity.Selection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BetRepository(private val betDao: BetDao): BaseRepository() {
    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    /***
     * 新增投注資料
     */
    fun insert(bean: BetBean) {
        scope.launch {
            betDao.insert(bean)
        }
    }

    /***
     * 更新投注盤口
     */
    fun updateSelection(id: Long, selection: Selection) {
        scope.launch {
            betDao.getBetById(id)?.let { bet ->
                bet.selection = selection
                betDao.update(bet)
            }
        }
    }

    /***
     * 更新盤口投注限額
     */
    fun updateLimitAmount(id: Long, min: Long, max: Long) {
        scope.launch {
            betDao.getBetById(id)?.let { bet ->
                bet.minAmount = min
                bet.maxAmount = max
                betDao.update(bet)
            }
        }
    }

    /***
     * 暫時關閉盤口
     */
    fun closeSelection(id: Long) {
        scope.launch {
            betDao.getBetById(id)?.let { bet ->
                bet.isBetStop = true
                betDao.update(bet)
            }
        }
    }

    /***
     * 開啟盤口
     */
    fun openSelection(id: Long) {
        scope.launch {
            betDao.getBetById(id)?.let { bet ->
                bet.isBetStop = false
                betDao.update(bet)
            }
        }
    }

    /***
     * 刪除投注資料
     */
    fun remove(id: Long) {
        scope.launch {
            betDao.removeBet(id)
        }
    }

    /***
     * 滾球
     */
    fun setPlaying(id: Long) {
        scope.launch {
            betDao.getBetById(id)?.let { bet ->
                bet.isPlaying = true
                betDao.update(bet)
            }
        }
    }
}