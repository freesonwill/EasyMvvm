package arch.cayenne.module.bet.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.common.utils.ext.SportMoneyOddsExt.getOdds
import arch.cayenne.lib.common.utils.ext.SportStringExt.toOdds
import arch.cayenne.lib.database.dao.BetDao
import arch.cayenne.lib.database.entity.BetDetailBean
import arch.cayenne.lib.database.entity.BetResultStatusEnum
import arch.cayenne.lib.database.entity.BetSelectionBean
import arch.cayenne.lib.database.entity.BetStatusEnum
import arch.cayenne.lib.database.entity.BetTypeEnum
import arch.cayenne.module.bet.BettingRemoteManager
import arch.cayenne.module.bet.data.ComboMultiBetBean
import arch.cayenne.module.bet.data.ComboMultiBetOddsBean
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
    manager: UserDataManager,
    private val remoteManager: BettingRemoteManager
) : BaseRepository() {

    private val selectionFlow =
        MutableSharedFlow<List<BetSelectionBean>>(replay = 1, extraBufferCapacity = 1)
    private val comboMultiBetFlow =
        MutableSharedFlow<List<ComboMultiBetBean>>(replay = 1, extraBufferCapacity = 1)

    private val observerOddsDisplay = manager.observe<Int>(UserDataKey.KEY_ODDS)
    private val observerLanguage = manager.observe<String>(UserDataKey.KEY_LANGUAGE)

    val isConnected: Boolean
        get() = remoteManager.isConnected

    init {
        scope.launch {
            launch {
                betDao.observeCurrentSelections().collect {
                    val lastSize = if (selectionFlow.replayCache.isEmpty()) {
                        0
                    } else {
                        selectionFlow.replayCache.first().size
                    }
                    setSelectionForCheckOdds(it)
                    if (lastSize != it.size) {
                        val emptyRisk = getEmptyRiskList(it.size)
                        if (emptyRisk.isNotEmpty()) {
                            comboMultiBetFlow.emit(calculateMultiBetSums(it, emptyRisk))
                        }
                        setComboMulti(it)
                    } else {
                        if (comboMultiBetFlow.replayCache.isNotEmpty()) {
                            val multi = comboMultiBetFlow.replayCache.first()
                            updateMultiOdds(it, multi)
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
                observerLanguage.collect {
                    updateLanguage()
                }
            }
        }
    }

    private fun getEmptyRiskList(size: Int): List<ComboRiskDataModel> {
        return if (size <= 1) {
            emptyList()
        } else if (size == 2) {
            listOf(
                ComboRiskDataModel(
                    serialValue = 2,
                    minAmount = 0L,
                    maxAmount = 0L
                )
            )
        } else{
            mutableListOf<ComboRiskDataModel>().apply {
                for (i in size downTo 1) {
                    add(
                        ComboRiskDataModel(
                            serialValue = if (i == 1) 0 else i,
                            minAmount = 0L,
                            maxAmount = 0L
                        )
                    )
                }
            }
        }
    }

    private suspend fun setComboMulti(
        data: List<BetSelectionBean>
    ) = withContext(scope.coroutineContext) {
        remoteManager.getComboRisk(data)?.let { riskList ->
            if (riskList.isNotEmpty()) {
                val multiBet = calculateMultiBetSums(data, riskList).map { bean ->
                    ComboMultiBetBean(
                        serialValue = bean.serialValue,
                        comboK = bean.comboK,
                        comboV = bean.comboV,
                        sumOdds = bean.sumOdds,
                        count = bean.count,
                        minAmount = bean.minAmount,
                        maxAmount = bean.maxAmount
                    )
                }
                comboMultiBetFlow.emit(multiBet)
            } else {
                comboMultiBetFlow.emit(calculateMultiBetSums(data, getEmptyRiskList(data.size)))
            }
        } ?: run {
            comboMultiBetFlow.emit(calculateMultiBetSums(data, getEmptyRiskList(data.size)))
        }
    }

    private fun updateMultiOdds(selections: List<BetSelectionBean>, multiBet: List<ComboMultiBetBean>) {
        val emptyRisk = getEmptyRiskList(selections.size)
        val newMulti = calculateMultiBetOddsSums(selections, emptyRisk)
        multiBet.forEach { multi ->
            newMulti[multi.serialValue]?.let { oddsBean ->
                multi.sumOdds = oddsBean.sumOdds
            } ?: run {
                multi.sumOdds
            }
        }
        scope.launch {
            comboMultiBetFlow.emit(multiBet)
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
                        .sumOf { it.reduce { acc, l ->
                            acc.getOdds(l).toOdds()
                        } }
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

        return result.sortedWith { a, b ->
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

    private fun calculateMultiBetOddsSums(
        data: List<BetSelectionBean>,
        riskList: List<ComboRiskDataModel>
    ): Map<Int, ComboMultiBetOddsBean> {
        val result = hashMapOf<Int, ComboMultiBetOddsBean>()
        val oddsList = data.map { it.odds }
        val n = data.size
//        val oddsDisplayEnum = getOddsDisplay()

        val riskMap = riskList.associateBy { it.serialValue }
        var totalSumOdds = 0

        for (k in n downTo 0) {
            riskMap[k]?.let { risk ->
                val odds = when (k) {
                    0 -> 0
                    else -> oddsList.combinations(k)
                        .sumOf { it.reduce { acc, l ->
                            acc.getOdds(l).toOdds()
                        } }
                }
                totalSumOdds += odds

                if (k == 0) {
                    result[risk.serialValue] = ComboMultiBetOddsBean(
                        serialValue = risk.serialValue,
                        sumOdds = totalSumOdds
                    )
                } else {
                    result[risk.serialValue] = ComboMultiBetOddsBean(
                        serialValue = risk.serialValue,
                        sumOdds = odds
                    )
                }
            }
        }
        return result
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

    suspend fun getBetSize(): Int = withContext(scope.coroutineContext) {
        betDao.getCurrentBet()?.let {
            betDao.getSelections(it.betId).size
        } ?: 0
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
                val selections = betDao.getSelections(bet.betId)
                val multi = comboMultiBetFlow.replayCache.firstOrNull()
                setSelectionForCheckOdds(selections)

                if (selections.isNotEmpty() && !multi.isNullOrEmpty()) {
                    updateMultiOdds(selections, multi)
                } else if (selections.isNotEmpty() && multi == null) {
                    calculateMultiBetSums(selections, getEmptyRiskList(selections.size)).let {
                        comboMultiBetFlow.emit(it)
                    }
                }
            }
        }
    }

    private suspend fun setSelectionForCheckOdds(selections: List<BetSelectionBean>) {
        selectionFlow.emit(selections)
    }
}