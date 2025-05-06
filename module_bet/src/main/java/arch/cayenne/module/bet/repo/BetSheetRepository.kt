package arch.cayenne.module.bet.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.database.dao.BetDao
import arch.cayenne.lib.skin.SportSkinManager
import arch.cayenne.module.bet.BettingRemoteManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BetSheetRepository(
    override val scope: CoroutineScope,
    private val betDao: BetDao,
    private val remoteManager: BettingRemoteManager,
    private val skinManager: SportSkinManager
): BaseRepository() {

    init {
        scope.launch {
            remoteManager.matchNotifyFlow.collect { data ->
                data.forEach { newSelection ->
                    betDao.getCurrentSelectionById(newSelection.selectionId)?.let { oldSelection ->
                        oldSelection.updateOdds(newSelection.odds)
                        oldSelection.isActive = newSelection.isActive
                        oldSelection.isParlay = newSelection.isParlay
                        betDao.updateSelection(oldSelection)
                    }
                }
            }
        }
        register()
    }

    suspend fun getBetType() = withContext(scope.coroutineContext) {
        betDao.getCurrentBet()?.betType
    }

    private fun register() {
        scope.launch {
            betDao.getCurrentBet()?.let { bet ->
                val selections = betDao.getSelections(bet.betId)
                remoteManager.registerMatchNotify(selections.map { it.matchId })
            }
        }
    }

    fun unregister() {
        scope.launch {
            betDao.getCurrentBet()?.let { bet ->
                val selections = betDao.getSelections(bet.betId)
                remoteManager.unregisterMatchNotify(selections.map { it.matchId })
                selections.forEach {
                    it.oddsStatus = null
                    betDao.updateSelection(it)
                }
            }
        }
    }
}