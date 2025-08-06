package arch.cayenne.module.bet.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.database.dao.BetDao
import arch.cayenne.lib.database.entity.BetDetailBean
import arch.cayenne.lib.database.entity.BetResultStatusEnum
import arch.cayenne.lib.database.entity.BetSelectionBean
import arch.cayenne.lib.database.entity.BetStatusEnum
import arch.cayenne.lib.database.entity.BetTypeEnum
import arch.cayenne.module.bet.BettingRemoteManager
import arch.cayenne.module.bet.data.ComboMultiBetBean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SingleBetRepository(
    override val scope: CoroutineScope,
    private val betDao: BetDao,
    manager: UserDataManager,
    private val remoteManager: BettingRemoteManager
) : BaseRepository() {

    private val selectionFlow =
        MutableSharedFlow<BetSelectionBean>(replay = 1, extraBufferCapacity = 1)
    private val comboFlow =
        MutableSharedFlow<ComboMultiBetBean>(replay = 1, extraBufferCapacity = 1)

    private val observerOddsDisplay = manager.observe<Int>(UserDataKey.KEY_ODDS)
    private val observerLanguage = manager.observe<String>(UserDataKey.KEY_LANGUAGE)

    val isConnected: Boolean
        get() = remoteManager.isConnected

    private var init = false

    init {
        scope.launch {
            launch {
                betDao.observeCurrentSelections().collect {
                    if (it.isNotEmpty() && it.size == 1) {
                        val data = it.first()
                        selectionFlow.emit(data)
                        if (!init) {
                            setComboMulti(data)
                        }
                        init = true
                    }
                }
            }
            launch {
                observerOddsDisplay.collect {
                    updateOdds()
                }
            }
            launch {
                observerLanguage.collect {
                    updateLanguage()
                }
            }
        }
    }

    private suspend fun setComboMulti(selection: BetSelectionBean) =
        withContext(scope.coroutineContext) {
            remoteManager.getSingleRisk(selection.matchId, selection.selectionId)?.let { risk ->
                if (risk.matchId == selection.matchId && risk.selectionId == selection.selectionId) {
                    val detailBean =
                        ComboMultiBetBean(
                            sumOdds = selection.odds,
                            minAmount = risk.minAmount,
                            maxAmount = risk.maxAmount,
                        )
                    comboFlow.emit(detailBean)
                }
            } ?: run {
                comboFlow.emit(
                    ComboMultiBetBean(
                        sumOdds = selection.odds,
                        minAmount = 0,
                        maxAmount = 0
                    )
                )
            }
        }

    fun observeSelectionBean(): Flow<BetSelectionBean> = selectionFlow
    fun observeComboBean(): Flow<ComboMultiBetBean> = comboFlow

    fun observeBetType(): Flow<BetTypeEnum?> = betDao.observeCurrentBetType()

    fun removeBet() {
        scope.launch {
            betDao.removeCurrentBet()
        }
    }

    fun saveToCombo() {
        scope.launch {
            betDao.getCurrentBet()?.let {
                betDao.updateBetType(it.betId, BetTypeEnum.COMBO)
            }
        }
    }

    suspend fun saveToReserve(odds: Int) = withContext(scope.coroutineContext) {
        betDao.getCurrentBet()?.let {
            betDao.insertDetail(
                BetDetailBean(
                    betId = it.betId,
                    sumOdds = odds,
                    inputMoney = 0L
                )
            )
            return@withContext betDao.updateBetType(it.betId, BetTypeEnum.RESERVE) == 1
        }
        false
    }

    fun sendBet(money: Long) {
        scope.launch {
            betDao.getCurrentBet()?.let {
                if (it.betType == BetTypeEnum.SINGLE) {
                    val betId = it.betId
                    betDao.updateBetStatus(betId, BetStatusEnum.BETTING)
                    val selection = betDao.getSelections(betId).first()

                    val tempDetail = BetDetailBean(
                        betId = betId,
                        orderId = "",
                        sumOdds = selection.odds,
                        inputMoney = money,
                        status = BetResultStatusEnum.CONFIRMING
                    )
                    betDao.insertDetail(tempDetail)
                    val resp = remoteManager.singleBet(selection, money)
                    val detailBean = if (resp != null && resp.isSuccessful) {
                        BetDetailBean(
                            betId = betId,
                            orderId = resp.orderId,
                            sumOdds = selection.odds,
                            inputMoney = money,
                            status = BetResultStatusEnum.getStatusByCode(resp.orderStatus)
                        )
                    } else {
                        BetDetailBean(
                            betId = betId,
                            orderId = "",
                            sumOdds = selection.odds,
                            inputMoney = money,
                            status = BetResultStatusEnum.CONFIRMING
                        )
                    }
                    betDao.insertDetail(detailBean)
                    betDao.updateBetStatus(betId, BetStatusEnum.COMPLETE)
                }
            }
        }
    }

    suspend fun saveToSingle() = withContext(scope.coroutineContext) {
        betDao.getCurrentBet()?.let { bet ->
            return@withContext betDao.updateBetType(bet.betId, BetTypeEnum.SINGLE) == 1
        }
        false
    }

    fun sendReserve(money: Long) {
        scope.launch {
            betDao.getCurrentBet()?.let { bet ->
                if (bet.betType == BetTypeEnum.RESERVE) {
                    val betId = bet.betId
                    betDao.updateBetStatus(betId, BetStatusEnum.BETTING)

                    val selection = betDao.getSelections(betId).first()
                    val detail = betDao.getDetail(betId).first()

                    val resp = remoteManager.reserveBet(selection, detail.sumOdds, money)
                    val status =
                        if (resp?.isSuccessful == true) BetResultStatusEnum.SUCCESS_BET else BetResultStatusEnum.REJECT

                    val detailBean = BetDetailBean(
                        betId = betId,
                        orderId = "",
                        sumOdds = detail.sumOdds,
                        inputMoney = money,
                        status = status
                    )
                    betDao.insertDetail(detailBean)
                    betDao.updateBetStatus(betId, BetStatusEnum.COMPLETE)
                }
            }
        }
    }

    private fun updateLanguage() {
        scope.launch {
            betDao.getCurrentBet()?.let { bet ->
                betDao.getSelections(bet.betId).forEach { selection ->
                    remoteManager.getMatchReq(selection.matchId)?.let { newMatch ->
                        newMatch.markets.find { market ->
                            market.selections.find { it.selectionId == selection.selectionId } != null
                        }?.let { market ->
                            val marketName = market.market.marketName
                            val name =
                                market.selections.find { it.selectionId == selection.selectionId }?.name
                                    ?: selection.name
                            val leagueName = newMatch.match.basicInfo.tournamentName
                            val matchName = newMatch.match.basicInfo.matchName
                            betDao.updateLanguage(
                                bet.betId,
                                selection.selectionId,
                                marketName = marketName,
                                name = name,
                                leagueName = leagueName,
                                matchName = matchName
                            )
                        }
                    }
                }
            }
        }
    }

    private fun updateOdds() {
        scope.launch {
            betDao.getCurrentBet()?.let { bet ->
                betDao.getSelections(bet.betId).forEach { selection ->
                    remoteManager.getMatchReq(selection.matchId)?.let { newMatch ->
                        newMatch.markets.find { market ->
                            market.selections.find { it.selectionId == selection.selectionId } != null
                        }?.selections?.find { it.selectionId == selection.selectionId }
                            ?.let { newSelection ->
                                betDao.updateOdds(
                                    bet.betId,
                                    selection.selectionId,
                                    newSelection.odds
                                )
                            }
                    }
                }
            }
        }
    }
}