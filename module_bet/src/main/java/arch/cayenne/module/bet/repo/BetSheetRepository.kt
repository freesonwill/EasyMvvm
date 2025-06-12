package arch.cayenne.module.bet.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.database.dao.BetDao
import arch.cayenne.lib.database.entity.BetTypeEnum
import arch.cayenne.module.bet.BettingRemoteManager
import galaxy.client.proto.Client
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BetSheetRepository(
    override val scope: CoroutineScope,
    private val betDao: BetDao,
    private val remoteManager: BettingRemoteManager
) : BaseRepository() {

    init {
        scope.launch {
            remoteManager.matchMarketNotifyFlow.collect { data ->
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
    }

    suspend fun getSelectionSize() = withContext(scope.coroutineContext) {
        betDao.getCurrentBet()?.let { bet ->
            betDao.getSelections(bet.betId).size
        } ?: 0
    }

    fun register() {
        scope.launch {
            betDao.getCurrentBet()?.let { bet ->
                val selections = betDao.getSelections(bet.betId)
                remoteManager.registerMatchMarketNotify(selections.map {
                    Client.MarketIdBase.newBuilder()
                        .setMatchId(it.matchId)
                        .addMarketId(it.marketId)
                        .build()
                })
            }
        }
    }

    fun unregister() {
        scope.launch {
            betDao.getCurrentBet()?.let { bet ->
                val selections = betDao.getSelections(bet.betId)
                remoteManager.unregisterMatchMarketNotify(selections.map {
                    Client.MarketIdBase.newBuilder()
                        .setMatchId(it.matchId)
                        .addMarketId(it.marketId)
                        .build()
                })
                selections.forEach {
                    it.oddsStatus = null
                    betDao.updateSelection(it)
                }
            }
        }
    }

    fun removeSingleBet() {
        scope.launch {
            betDao.getCurrentBet()?.let {
                if (it.betType == BetTypeEnum.SINGLE || it.betType == BetTypeEnum.RESERVE) {
                    betDao.removeBet(it.betId)
                    betDao.removeBetSelection(it.betId)
                    betDao.removeBetDetail(it.betId)
                }
            }
        }
    }
}