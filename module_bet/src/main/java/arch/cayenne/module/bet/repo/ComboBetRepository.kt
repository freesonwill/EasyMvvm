package arch.cayenne.module.bet.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.common.utils.ext.SportIntExt.getOdds
import arch.cayenne.lib.common.utils.ext.SportStringExt.toOdds
import arch.cayenne.lib.database.dao.BetDao
import arch.cayenne.lib.database.entity.BetDetailBean
import arch.cayenne.lib.database.entity.BetResultStatusEnum
import arch.cayenne.lib.database.entity.BetSelectionBean
import arch.cayenne.lib.database.entity.BetStatusEnum
import arch.cayenne.lib.database.entity.BetTypeEnum
import arch.cayenne.module.bet.BettingRemoteManager
import arch.cayenne.module.bet.data.ComboMultiBetBean
import arch.cayenne.module.bet.data.remote.ComboRiskDataModel
import galaxy.client.proto.Client
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ComboBetRepository(
    override val scope: CoroutineScope,
    private val betDao: BetDao,
    private val remoteManager: BettingRemoteManager
) : BaseRepository() {

    private val selectionFlow =
        MutableSharedFlow<List<BetSelectionBean>>(replay = 1, extraBufferCapacity = 1)
    private val comboMultiBetFlow =
        MutableSharedFlow<List<ComboMultiBetBean>>(replay = 1, extraBufferCapacity = 1)

    init {
        scope.launch {
            betDao.getCurrentBet()?.let { bet ->
                launch {
                    betDao.observeSelections(bet.betId).collect {
                        selectionFlow.emit(it)
                    }
                }
                // TODO 之後可能改為盤口變動就須獲取限額
                launch {
                    val selection = betDao.getSelections(bet.betId)
                    if (selection.isNotEmpty()) {
                        val detail = betDao.getDetail(bet.betId)
                        setComboMulti(selection, detail)
                    }
                }
            }
        }
    }

    private suspend fun setComboMulti(
        data: List<BetSelectionBean>,
        detailList: List<BetDetailBean>? = null
    ) = withContext(scope.coroutineContext) {
        remoteManager.getComboRisk(data)?.let { riskList ->
            val multiBet = calculateMultiBetSums(data, riskList).map { bean ->
                val detail = detailList?.find { it.serialValue == bean.serialValue }
                if (detail == null) {
                    bean
                } else {
                    ComboMultiBetBean(
                        serialValue = bean.serialValue,
                        comboK = bean.comboK,
                        comboV = bean.comboV,
                        sumOdds = bean.sumOdds,
                        count = bean.count,
                        inputMoney = detail.inputMoney,
                        minAmount = bean.minAmount,
                        maxAmount = bean.maxAmount
                    )
                }
            }
            comboMultiBetFlow.emit(multiBet)
        } ?: run {
            comboMultiBetFlow.emit(emptyList())
        }
    }

    fun observeComboBet(): Flow<List<BetSelectionBean>> = selectionFlow
    fun observeComboMultiBet(): Flow<List<ComboMultiBetBean>> = comboMultiBetFlow

    fun removeSelection(selectionId: Long) {
        scope.launch {
            betDao.getCurrentBet()?.let { bet ->
                val selection =
                    betDao.getSelections(bet.betId).find { it.selectionId == selectionId }
                if (selection != null) {
                    betDao.removeBetSelectionByMatchId(bet.betId, selection.matchId)
                    remoteManager.unregisterMatchMarketNotify(
                        listOf(
                            Client.MarketIdBase.newBuilder()
                                .setMatchId(selection.matchId)
                                .addMarketId(selection.marketId)
                                .build()
                        )
                    )
                    val selections = betDao.getSelections(bet.betId)
                    setComboMulti(selections)
                }
            }
        }
    }

    fun removeAll() {
        scope.launch {
            betDao.removeCurrentBet()
        }
    }

    fun saveInputMoney(data: List<ComboMultiBetBean>) {
        scope.launch {
            betDao.getCurrentBet()?.let { bet ->
                if (bet.betType == BetTypeEnum.COMBO) {
                    val betId = bet.betId
                    val detailBean = data.map { bean ->
                        BetDetailBean(
                            serialValue = bean.serialValue,
                            betId = betId,
                            comboK = bean.comboK,
                            comboV = bean.comboV,
                            orderId = "",
                            sumOdds = bean.sumOdds,
                            count = bean.count,
                            inputMoney = bean.inputMoney
                        )
                    }
                    betDao.insertDetail(detailBean)
                }
            }
        }
    }

    fun sendBet(multiBet: List<ComboMultiBetBean>) {
        scope.launch {
            betDao.getCurrentBet()?.let { bet ->
                if (bet.betType == BetTypeEnum.COMBO) {
                    val betId = bet.betId
                    betDao.updateBetStatus(betId, BetStatusEnum.BETTING)

                    val selection = betDao.getSelections(betId)
                    val tempDetail = multiBet.map { bean ->
                        BetDetailBean(
                            serialValue = bean.serialValue,
                            betId = betId,
                            comboK = bean.comboK,
                            comboV = bean.comboV,
                            orderId = "",
                            sumOdds = bean.sumOdds,
                            count = bean.count,
                            inputMoney = bean.inputMoney,
                            status = BetResultStatusEnum.CONFIRMING
                        )
                    }
                    betDao.insertDetail(tempDetail)

                    val resp = remoteManager.comboBet(selection, multiBet)
                    val detailBean = if (resp != null && resp.isSuccessful) {
                        multiBet.map { bean ->
                            val res = resp.data.first { it.serialValue == bean.serialValue }
                            BetDetailBean(
                                serialValue = bean.serialValue,
                                betId = betId,
                                comboK = bean.comboK,
                                comboV = bean.comboV,
                                orderId = res.orderId,
                                sumOdds = bean.sumOdds,
                                count = bean.count,
                                inputMoney = bean.inputMoney,
                                status = BetResultStatusEnum.getStatusByCode(res.orderStatus)
                            )
                        }
                    } else {
                        multiBet.map { bean ->
                            BetDetailBean(
                                serialValue = bean.serialValue,
                                betId = betId,
                                comboK = bean.comboK,
                                comboV = bean.comboV,
                                orderId = "",
                                sumOdds = bean.sumOdds,
                                count = bean.count,
                                inputMoney = bean.inputMoney,
                                status = BetResultStatusEnum.FAIL
                            )
                        }
                    }
                    betDao.insertDetail(detailBean)
                    betDao.updateBetStatus(betId, BetStatusEnum.COMPLETE)
                }
            }
        }
    }

    private fun calculateMultiBetSums(
        data: List<BetSelectionBean>,
        riskList: List<ComboRiskDataModel>
    ): List<ComboMultiBetBean> {
        val result = mutableListOf<ComboMultiBetBean>()
        val oddsList = data.map { it.odds }
        val n = data.size

        val riskMap = riskList.associateBy { it.serialValue }
        var totalSumOdds = 0
        var totalCount = 0

        for (k in n downTo 0) {
            riskMap[k]?.let { risk ->
                val combinations = data.combinations(k)
                val odds = when (k) {
                    0 -> 0
                    else -> oddsList.combinations(k)
                        .sumOf { it.reduce { acc, l -> acc.getOdds(l).toOdds() } }
                }
                val count = when (k) {
                    0 -> 0
                    else -> combinations.size
                }
                totalSumOdds += odds
                totalCount += count

                if (k == 0) {
                    result.add(
                        ComboMultiBetBean(
                            serialValue = risk.serialValue,
                            comboK = n,
                            comboV = totalCount,
                            sumOdds = totalSumOdds,
                            count = totalCount,
                            minAmount = risk.minAmount,
                            maxAmount = risk.maxAmount
                        )
                    )
                } else {
                    result.add(
                        ComboMultiBetBean(
                            serialValue = risk.serialValue,
                            comboK = k,
                            comboV = 1,
                            sumOdds = odds,
                            count = count,
                            minAmount = risk.minAmount,
                            maxAmount = risk.maxAmount
                        )
                    )
                }

            }
        }
        return result.sortedWith(compareBy({ it.comboK }, { it.comboV }))
    }

    private fun <T> List<T>.combinations(k: Int): List<List<T>> {
        if (k == 0) return listOf(emptyList())
        if (this.isEmpty()) return emptyList()

        val head = first()
        val tail = drop(1)

        val withHead = tail.combinations(k - 1).map { listOf(head) + it }
        val withoutHead = tail.combinations(k)

        return withHead + withoutHead
    }
}