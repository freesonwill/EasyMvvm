package arch.cayenne.module.bet.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
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

class SingleBetRepository(
    override val scope: CoroutineScope,
    private val betDao: BetDao,
    private val remoteManager: BettingRemoteManager
) : BaseRepository() {

    private val selectionFlow = MutableSharedFlow<BetSelectionBean>(replay = 1, extraBufferCapacity = 1)
    private val comboFlow = MutableSharedFlow<ComboMultiBetBean>(replay = 1, extraBufferCapacity = 1)

    init {
        scope.launch {
            betDao.getCurrentBet()?.let { bet ->
                betDao.observeSelections(bet.betId).collect {
                    val data = it.firstOrNull() ?: return@collect
                    selectionFlow.emit(data)
                    if (it.isNotEmpty()) {
                        val lastDetail = betDao.getDetail(bet.betId).firstOrNull()
                        setComboMulti(data, lastDetail)
                    }
                }
            }
        }
    }

    private suspend fun setComboMulti(selection: BetSelectionBean, detailList: BetDetailBean? = null) {
        remoteManager.getSingleRisk(selection.matchId, selection.selectionId)?.let { risk ->
            if (risk.matchId == selection.matchId && risk.selectionId == selection.selectionId) {
                val detailBean = if (detailList == null) {
                    ComboMultiBetBean(
                        sumOdds = selection.odds,
                        minAmount = risk.minAmount,
                        maxAmount = risk.maxAmount
                    )
                } else {
                    ComboMultiBetBean(
                        sumOdds = selection.odds,
                        inputMoney = detailList.inputMoney,
                        minAmount = risk.minAmount,
                        maxAmount = risk.maxAmount,
                    )
                }
                comboFlow.emit(detailBean)
            }
        }
    }

    fun observeSelectionBean(): Flow<BetSelectionBean> = selectionFlow
    fun observeComboBean(): Flow<ComboMultiBetBean> = comboFlow

    fun removeBet() {
        scope.launch {
            betDao.removeCurrentBet()
        }
    }

    fun removeSingleBet() {
        scope.launch {
            betDao.getCurrentBet()?.let {
                if (it .betType == BetTypeEnum.SINGLE) {
                    betDao.removeBet(it.betId)
                    betDao.removeBetSelection(it.betId)
                    betDao.removeBetDetail(it.betId)
                }
            }
        }
    }

    fun saveToCombo() {
        scope.launch {
            betDao.getCurrentBet()?.let {
                betDao.updateBetType(it.betId, BetTypeEnum.COMBO)
            }
        }
    }

    fun saveReserve(odds: Int) {
        scope.launch {
            betDao.getCurrentBet()?.let {
                if (it.betType == BetTypeEnum.SINGLE) {
                    betDao.insertDetail(
                        BetDetailBean(
                            betId = it.betId,
                            sumOdds = odds,
                            inputMoney = 0L
                        )
                    )
                    betDao.updateBetType(it.betId, BetTypeEnum.RESERVE)
                }
            }
        }
    }

    fun saveInputMoney(money: Long) {
        scope.launch {
            betDao.getCurrentBet()?.let {
                if (it.betType == BetTypeEnum.SINGLE) {
                    val betId = it.betId
                    val selection = betDao.getSelections(betId).firstOrNull()
                    if (selection != null) {
                        val detailBean = BetDetailBean(
                            betId = betId,
                            sumOdds = selection.odds,
                            inputMoney = money
                        )
                        betDao.insertDetail(detailBean)
                    }
                }
            }
        }
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

}