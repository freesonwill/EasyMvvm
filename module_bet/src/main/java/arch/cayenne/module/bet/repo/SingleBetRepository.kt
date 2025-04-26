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

    private val selectionFlow = MutableSharedFlow<BetSelectionBean>()
    private val comboFlow = MutableSharedFlow<ComboMultiBetBean>()

    init {
        scope.launch {
            betDao.getCurrentBet()?.let { bet ->
                val selection = betDao.getSelections(bet.betId).firstOrNull() ?: return@let
                remoteManager.getSingleRisk(selection.matchId, selection.selectionId)?.let { risk ->
                        if (risk.matchId == selection.matchId && risk.selectionId == selection.selectionId) {
                            val lastDetail = betDao.getDetail(bet.betId).firstOrNull()
                            val detailBean = if (lastDetail == null) {
                                ComboMultiBetBean(
                                    sumOdds = selection.odds,
                                    minAmount = risk.minAmount,
                                    maxAmount = risk.maxAmount
                                )
                            } else {
                                ComboMultiBetBean(
                                    sumOdds = selection.odds,
                                    inputMoney = lastDetail.inputMoney,
                                    minAmount = risk.minAmount,
                                    maxAmount = risk.maxAmount,
                                )
                            }
                            selectionFlow.emit(selection)
                            comboFlow.emit(detailBean)
                        }
                    }
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

    fun saveToCombo() {
        scope.launch {
            betDao.getCurrentBet()?.let {
                betDao.updateBetType(it.betId, BetTypeEnum.COMBO)
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
                            status = BetResultStatusEnum.REJECT
                        )
                    }
                    betDao.insertDetail(detailBean)
                    betDao.updateBetStatus(betId, BetStatusEnum.COMPLETE)
                }
            }
        }
    }

}