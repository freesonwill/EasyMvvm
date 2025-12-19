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
import arch.cayenne.module.bet.data.OddsChangeEnum
import galaxy.client.proto.Client
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.onStart
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

    private val oddsChangeFlow =
        MutableSharedFlow<OddsChangeEnum>(replay = 1, extraBufferCapacity = 1)

    init {
        scope.launch {
            launch {
                observerLanguage.collect {
                    updateLanguage()
                }
            }
            launch {
                betDao.observeCurrentSelections().collect {
                    if (it.isNotEmpty() && it.size == 1) {
                        val data = it.first()
                        val isInit = comboFlow.replayCache.isEmpty()
                        setSelectionForCheckOdds(data)
                        if (isInit) {
                            setComboMulti(data)
                        }
                    }
                }
            }
            launch {
                observerOddsDisplay.collect {
                    updateOdds()
                }
            }
            launch {
                manager.observe<Int>(UserDataKey.KEY_ODDS_CHANGE)
                    .onStart {
                        val value =
                            manager.getValue(UserDataKey.KEY_ODDS_CHANGE, OddsChangeEnum.ANY.value)
                        val odds = OddsChangeEnum.fromValue(value)
                        oddsChangeFlow.emit(odds)
                    }
                    .collect { oddsValue ->
                        val odds = OddsChangeEnum.fromValue(oddsValue)
                        oddsChangeFlow.emit(odds)
                    }
            }
        }
    }

    private fun setSelectionForCheckOdds(selection: BetSelectionBean) {
        scope.launch {
            selectionFlow.emit(selection)
            betDao.getCurrentBet()?.let {
                val detail = betDao.getDetail(it.betId).firstOrNull()
                if (detail != null) {
                    betDao.updateDetailOdds(it.betId, detail.serialValue, selection.odds)
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
                            odds = selection.odds,
                            minAmount = risk.minAmount,
                            maxAmount = risk.maxAmount,
                        )
                    comboFlow.emit(detailBean)
                }
            } ?: run {
                comboFlow.emit(
                    ComboMultiBetBean(
                        sumOdds = selection.odds,
                        odds = selection.odds,
                        minAmount = 0,
                        maxAmount = 0
                    )
                )
            }
        }

    fun observeSelectionBean(): Flow<BetSelectionBean> = selectionFlow
    fun observeComboBean(): Flow<ComboMultiBetBean> = comboFlow

    fun observeBetType(): Flow<BetTypeEnum?> = betDao.observeCurrentBetType()

    fun observeOddsChange(): Flow<OddsChangeEnum> = oddsChangeFlow

    fun removeBet() {
        scope.launch {
            betDao.removeCurrentBet()
        }
    }

    suspend fun saveToCombo(): Boolean = withContext(Dispatchers.IO) {
        betDao.getCurrentBet()?.let {
            betDao.updateBetType(it.betId, BetTypeEnum.COMBO)
        }
        true
    }

    suspend fun saveToReserve(odds: Int, money: Long) = withContext(Dispatchers.IO) {
        betDao.getCurrentBet()?.let {
            val selection = betDao.getSelections(it.betId).first()
            betDao.updateOdds(it.betId, selection.selectionId, odds)
            betDao.insertDetail(
                BetDetailBean(
                    betId = it.betId,
                    sumOdds = odds,
                    odds = odds,
                    inputMoney = money
                )
            )
            return@withContext betDao.updateBetType(it.betId, BetTypeEnum.RESERVE) == 1
        }
        false
    }

    private suspend fun setMoney(betId: Long, money: Long) {
        val detail = betDao.getDetail(betId).firstOrNull()
        if (detail != null) {
            betDao.updateDetailMoney(betId, detail.serialValue, money)
        } else {
            val selection = betDao.getSelections(betId)
            if (selection.isNotEmpty()) {
                val data = selection.first()
                val newDetail = BetDetailBean(
                    betId = betId,
                    sumOdds = data.odds,
                    odds = data.odds,
                    inputMoney = money
                )
                betDao.insertDetail(newDetail)
            }
        }
    }

    fun sendBet(money: Long, oddsChange: OddsChangeEnum) {
        scope.launch {
            betDao.getCurrentBet()?.let {
                if (it.betType == BetTypeEnum.SINGLE) {
                    val betId = it.betId
                    betDao.updateBetStatus(betId, BetStatusEnum.BETTING)
                    val selection = betDao.getSelections(betId).first()
                    unregister(selection)

                    val tempDetail = betDao.getDetail(betId).firstOrNull() ?: run {
                        BetDetailBean(
                            betId = betId,
                            sumOdds = selection.odds,
                            odds = selection.odds,
                            inputMoney = money
                        ).apply {
                            betDao.insertDetail(this)
                        }
                    }
                    betDao.updateDetailStatus(
                        tempDetail.betId,
                        tempDetail.serialValue,
                        BetResultStatusEnum.CONFIRMING
                    )
                    val resp = remoteManager.singleBet(selection, money, oddsChange)
                    if (resp != null && resp.isSuccessful) {
                        tempDetail.orderId = resp.orderId
                        tempDetail.status = BetResultStatusEnum.getStatusByCode(resp.orderStatus)
                        betDao.updateDetail(tempDetail)
                    } else {
                        tempDetail.status =
                            if (resp != null && !resp.isSuccessful) BetResultStatusEnum.REJECT else BetResultStatusEnum.CONFIRMING
                        betDao.updateDetail(tempDetail)
                    }
                    betDao.getCurrentBet(BetStatusEnum.BETTING)?.let { bettingBet ->
                        betDao.updateBetStatus(bettingBet.betId, BetStatusEnum.COMPLETE)
                    }
                }
            }
        }
    }

    private fun unregister(selection: BetSelectionBean) {
        scope.launch {
            remoteManager.unregisterMatchMarketNotify(
                listOf(
                    Client.MarketIdBase.newBuilder()
                        .setMatchId(selection.matchId)
                        .addMarketId(selection.marketId)
                        .build()
                )
            )
            selection.oddsStatus = null
            betDao.updateSelection(selection)

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
                    unregister(selection)
                    val detail = betDao.getDetail(betId).firstOrNull() ?: run {
                        BetDetailBean(
                            betId = betId,
                            sumOdds = selection.odds,
                            odds = selection.odds,
                            inputMoney = money
                        ).apply {
                            betDao.insertDetail(this)
                        }
                    }
                    betDao.updateDetailStatus(
                        detail.betId,
                        detail.serialValue,
                        BetResultStatusEnum.CONFIRMING
                    )
                    val resp = remoteManager.reserveBet(selection, detail.sumOdds, money)
                    val status =
                        if (resp?.isSuccessful == true) BetResultStatusEnum.SUCCESS_BET else BetResultStatusEnum.REJECT


                    betDao.updateDetailStatus(detail.betId, detail.serialValue, status)
                    betDao.getCurrentBet(BetStatusEnum.BETTING)?.let { bettingBet ->
                        betDao.updateBetStatus(bettingBet.betId, BetStatusEnum.COMPLETE)
                    }
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
                betDao.getSelections(bet.betId).let { selection ->
                    if (selection.isNotEmpty() && selection.size == 1) {
                        val data = selection.first()
                        setSelectionForCheckOdds(data)
                    }
                }
            }
        }
    }
}