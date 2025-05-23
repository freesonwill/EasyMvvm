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

class ReserveRepository(
    override val scope: CoroutineScope,
    private val betDao: BetDao,
    private val remoteManager: BettingRemoteManager
) : BaseRepository() {

    private val selectionFlow = MutableSharedFlow<BetSelectionBean>(replay = 1, extraBufferCapacity = 1)
    private val comboFlow = MutableSharedFlow<ComboMultiBetBean>(replay = 1, extraBufferCapacity = 1)

    init {
        scope.launch {
            betDao.getCurrentBet()?.let { bet ->
                val selection = betDao.getSelections(bet.betId).firstOrNull() ?: return@let
                remoteManager.getSingleRisk(selection.matchId, selection.selectionId)?.let { risk ->
                    if (risk.matchId == selection.matchId && risk.selectionId == selection.selectionId) {
                        betDao.getDetail(bet.betId).firstOrNull()?.let { lastDetail ->
                            val detailBean = ComboMultiBetBean(
                                sumOdds = lastDetail.sumOdds,
                                inputMoney = lastDetail.inputMoney,
                                minAmount = risk.minAmount,
                                maxAmount = risk.maxAmount,
                            )
                            comboFlow.emit(detailBean)
                        }
                        selectionFlow.emit(selection)
                    }
                }
            }
        }
    }

    fun observeSelectionBean(): Flow<BetSelectionBean> = selectionFlow
    fun observeComboBean(): Flow<ComboMultiBetBean> = comboFlow

    fun setSingleToReserve() {
        scope.launch {
            betDao.getCurrentBet()?.let {
                if (it.betType == BetTypeEnum.SINGLE) {
                    betDao.updateBetType(it.betId, BetTypeEnum.SINGLE)
                }
            }
        }
    }

    fun removeReserve() {
        scope.launch {
            betDao.getCurrentBet()?.let { bet ->
                if (bet.betType == BetTypeEnum.RESERVE) {
                    betDao.updateBetType(bet.betId, BetTypeEnum.SINGLE)
                }
            }
        }
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
}