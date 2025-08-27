package arch.cayenne.module.bet.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.database.dao.BetDao
import arch.cayenne.lib.database.dao.InfoDao
import arch.cayenne.lib.database.entity.BetBean
import arch.cayenne.lib.database.entity.BetDetailBean
import arch.cayenne.lib.database.entity.BetSelectionBean
import arch.cayenne.lib.database.entity.BetStatusEnum
import arch.cayenne.lib.database.entity.BetTypeEnum
import arch.cayenne.module.bet.BettingRemoteManager
import galaxy.client.proto.Client
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BetResultRepository(
    override val scope: CoroutineScope,
    private val infoDao: InfoDao,
    private val betDao: BetDao,
    private val remoteManager: BettingRemoteManager
) : BaseRepository() {

    private val betTypeFlow =
        MutableSharedFlow<BetTypeEnum>(replay = 1, extraBufferCapacity = 1)
    private val selectionFlow =
        MutableSharedFlow<List<BetSelectionBean>>(replay = 1, extraBufferCapacity = 1)
    private val detailFlow =
        MutableSharedFlow<List<BetDetailBean>>(replay = 1, extraBufferCapacity = 1)

    init {
        scope.launch {
            launch {
                betDao.observeCurrentBetType().distinctUntilChanged().collect { type ->
                    type?.let {
                        betTypeFlow.emit(it)
                    }
                }
            }
            launch {
                betDao.observeCurrentSelections().distinctUntilChanged().collect { selections ->
                    if (selections.isNotEmpty()) {
                        selectionFlow.emit(selections)
                    }
                }
            }
            launch {
                betDao.observeCurrentDetail().distinctUntilChanged().collect { detail ->
                    if (detail.isNotEmpty()) {
                        detailFlow.emit(sortDetail(detail))
                    }
                }
            }
        }
    }

    fun observeBetType(): Flow<BetTypeEnum> = betTypeFlow
    fun observeSelections(): Flow<List<BetSelectionBean>> = selectionFlow
    fun observeDetail(): Flow<List<BetDetailBean>> = detailFlow

    private fun sortDetail(data: List<BetDetailBean>): List<BetDetailBean> {
        val n = data.size
        return data.sortedWith { a, b ->
            val aIsOne = a.comboV == 1
            val bIsOne = b.comboV == 1

            val aIsMain = aIsOne && a.comboK == n
            val bIsMain = bIsOne && b.comboK == n

            when {
                // 優先顯示 maxComboK 且 comboV == 1 的那一筆
                aIsMain && !bIsMain -> -1
                !aIsMain && bIsMain -> 1

                // 接著顯示其他 comboV == 1 的，comboK 升序
                aIsOne && bIsOne -> a.comboK.compareTo(b.comboK)

                // comboV == 1 的優先於 comboV != 1
                aIsOne && !bIsOne -> -1
                !aIsOne && bIsOne -> 1

                // 最後 comboV != 1 的，依 comboK 升序，再 comboV 升序
                else -> {
                    val k = a.comboK.compareTo(b.comboK)
                    if (k != 0) k else a.comboV.compareTo(b.comboV)
                }
            }
        }
    }

    suspend fun getCurrency(): String = withContext(scope.coroutineContext) {
        infoDao.getCurrency()
    }

    suspend fun continueBet(): BetTypeEnum? = withContext(scope.coroutineContext) {
        val lastBet = betDao.getLastBetOrder() ?: return@withContext null

        val selectionList = betDao.getSelections(lastBet.betId)
        val detailList = betDao.getDetail(lastBet.betId)

        val newBet = BetBean(
            betType = lastBet.betType
        )
        val newBetId = betDao.insert(newBet)

        val newSelections = selectionList.map { selection ->
            selection.copy(betId = newBetId)
        }
        val newDetails = detailList.map { detail ->
            detail.copy(
                betId = newBetId,
                orderId = "",
                status = null
            )
        }

        betDao.insertSelection(newSelections)
        betDao.insertDetail(newDetails)

        register(newSelections)
        betDao.updateBetStatus(lastBet.betId, BetStatusEnum.DONE)

        newBet.betType
    }

    private fun register(selections: List<BetSelectionBean>) {
        scope.launch {
            remoteManager.registerMatchMarketNotify(selections.map {
                Client.MarketIdBase.newBuilder()
                    .setMatchId(it.matchId)
                    .addMarketId(it.marketId)
                    .build()
            })
        }
    }

    fun sendDone() {
        scope.launch {
            val lastBet = betDao.getLastBetOrder() ?: return@launch
            betDao.updateBetStatus(lastBet.betId, BetStatusEnum.DONE)
        }
    }
}