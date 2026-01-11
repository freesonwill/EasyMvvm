package arch.cayenne.module.bet.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.database.dao.BetDao
import arch.cayenne.lib.database.dao.InfoDao
import arch.cayenne.lib.database.dao.SportLoginInfoDao
import arch.cayenne.lib.database.entity.BetTypeEnum
import arch.cayenne.module.bet.BettingRemoteManager
import galaxy.client.proto.Client
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BetSheetRepository(
    override val scope: CoroutineScope,
    private val infoDao: InfoDao,
    private val betDao: BetDao,
    private val sportLoginInfoDao: SportLoginInfoDao,
    private val remoteManager: BettingRemoteManager,
    private val userDataManager: UserDataManager,
    ) : BaseRepository() {

    val observerBetCount: Flow<Int> = betDao.observeCurrentCount()

    private var registerObserverJob: Job? = null
    private var loginStatusObserverJob: Job? = null

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
        registerObserverJob?.cancel()
        registerObserverJob = scope.launch {
            betDao.observeCurrentBet().collect { betBean ->
                betBean?.let { bet ->
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
        registerObserverJob?.cancel()
        registerObserverJob = null
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

    fun observeLoginStatus() {
        loginStatusObserverJob?.cancel()
        loginStatusObserverJob = scope.launch {
            sportLoginInfoDao.observerLogin().collect {
                if (it == true) {
                    register()
                }
            }
        }
    }

    fun stopObserveLoginStatus() {
        loginStatusObserverJob?.cancel()
        loginStatusObserverJob = null
    }

    suspend fun getBetType(): BetTypeEnum? = withContext(scope.coroutineContext) {
        betDao.getCurrentBet()?.betType
    }
}