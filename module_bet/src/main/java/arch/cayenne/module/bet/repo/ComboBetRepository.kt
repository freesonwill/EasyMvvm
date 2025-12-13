package arch.cayenne.module.bet.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.common.utils.ext.CombinationExt
import arch.cayenne.lib.common.utils.ext.CombinationExt.combination
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.database.dao.BetDao
import arch.cayenne.lib.database.entity.BetDetailBean
import arch.cayenne.lib.database.entity.BetResultStatusEnum
import arch.cayenne.lib.database.entity.BetSelectionBean
import arch.cayenne.lib.database.entity.BetStatusEnum
import arch.cayenne.lib.database.entity.BetTypeEnum
import arch.cayenne.module.bet.BettingRemoteManager
import arch.cayenne.module.bet.R
import arch.cayenne.module.bet.data.ComboMultiBetBean
import arch.cayenne.module.bet.data.ComboMultiBetOddsBean
import arch.cayenne.module.bet.data.OddsChangeEnum
import arch.cayenne.module.bet.data.remote.ComboRiskDataModel
import arch.cayenne.module.bet.ui.fragment.ComboDetailFragment
import arch.cayenne.module.bet.util.BetUtils.calculateCombinationOdds
import galaxy.client.proto.Client
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.onStart
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

    private val oddsChangeFlow =
        MutableSharedFlow<OddsChangeEnum>(replay = 1, extraBufferCapacity = 1)

    val isConnected: Boolean
        get() = remoteManager.isConnected

    init {
        scope.launch {
            launch {
                betDao.observeCurrentSelections().collect {
                    if (it.size > 1) {
                        val lastSize = if (selectionFlow.replayCache.isEmpty()) {
                            0
                        } else {
                            selectionFlow.replayCache.first().size
                        }
                        setSelectionForCheckOdds(it)
                        if (lastSize != it.size) {
                            val emptyRisk = getEmptyRiskList(it.size)
                            if (emptyRisk.isNotEmpty()) {
                                //"aaaa---betDao.observeCurrentSelections():$emptyRisk--it:$it".logd(TAG)
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
                    //不复制inputMoney
                    ComboMultiBetBean(
                        serialValue = bean.serialValue,
                        comboK = bean.comboK,
                        comboV = bean.comboV,
                        sumOdds = bean.sumOdds,
                        odds = bean.odds,
                        count = bean.count,
                        minAmount = bean.minAmount,
                        maxAmount = bean.maxAmount,
                    )
                }
                saveDetail(multiBet)
                //"aaaa---setComboMulti1--$data".logd(TAG)
                comboMultiBetFlow.emit(multiBet)
            } else {
                //"aaaa---setComboMulti2--$data".logd(TAG)
                comboMultiBetFlow.emit(calculateMultiBetSums(data, getEmptyRiskList(data.size)))
            }
        } ?: run {
            //"aaaa---setComboMulti3--$data".logd(TAG)
            comboMultiBetFlow.emit(calculateMultiBetSums(data, getEmptyRiskList(data.size)))
        }
    }

    private suspend fun saveDetail(multiBet: List<ComboMultiBetBean>) {
        betDao.getCurrentBet()?.let { bet ->
            val betId = bet.betId
            val currentDetail = betDao.getDetail(betId)
            multiBet.forEach { multiBet ->
                if (currentDetail.find { it.serialValue == multiBet.serialValue && it.comboK == multiBet.comboK && it.comboV == multiBet.comboV && it.count == multiBet.count} == null) {
                    BetDetailBean(
                        serialValue = multiBet.serialValue,
                        betId = betId,
                        comboK = multiBet.comboK,
                        comboV = multiBet.comboV,
                        sumOdds = multiBet.sumOdds,
                        odds = multiBet.odds,
                        count = multiBet.count,
                        inputMoney = 0L
                    ).apply {
                        betDao.insertDetail(this)
                    }
                } else {
                    betDao.updateDetailOdds(betId, multiBet.serialValue, multiBet.sumOdds)
                }
            }
        }
    }

    private suspend fun updateMultiOdds(selections: List<BetSelectionBean>, multiBet: List<ComboMultiBetBean>) {
        //"aaaa---updateMultiOdds,selections:$selections,\t multiBet:$multiBet".logd(TAG)
        val emptyRisk = getEmptyRiskList(selections.size)
        val newMulti = calculateMultiBetOddsSums(selections, emptyRisk)
        multiBet.forEach { multi ->
            newMulti[multi.serialValue]?.let { oddsBean ->
                multi.sumOdds = oddsBean.sumOdds
            } ?: run {
                multi.sumOdds
            }
        }
        saveDetail(multiBet)
        comboMultiBetFlow.emit(multiBet)
    }

    fun observeComboBet(): Flow<List<BetSelectionBean>> = selectionFlow
    fun observeComboMultiBet(): Flow<List<ComboMultiBetBean>> = comboMultiBetFlow

    fun observeOddsChange(): Flow<OddsChangeEnum> = oddsChangeFlow

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
                }
            }
        }
    }

    fun removeAll() {
        scope.launch {
            betDao.removeCurrentBet()
        }
    }

    fun sendBet(multiBet: List<ComboMultiBetBean>, oddsChangeEnum: OddsChangeEnum) {
        scope.launch {
            betDao.getCurrentBet()?.let { bet ->
                if (bet.betType == BetTypeEnum.COMBO) {
                    val betId = bet.betId
                    betDao.updateBetStatus(betId, BetStatusEnum.BETTING)

                    val selection = betDao.getSelections(betId)
                    unregister(selection)
                    val currentDetail = betDao.getDetail(betId)
                    val tempDetail = currentDetail.ifEmpty {
                        multiBet.map { bean ->
                            BetDetailBean(
                                serialValue = bean.serialValue,
                                betId = betId,
                                comboK = bean.comboK,
                                comboV = bean.comboV,
                                orderId = "",
                                sumOdds = bean.sumOdds,
                                odds = bean.odds,
                                count = bean.count,
                                inputMoney = bean.inputMoney,
                                status = BetResultStatusEnum.CONFIRMING
                            )
                        }.apply {
                            betDao.insertDetail(this)
                        }
                    }


                    val resp = remoteManager.comboBet(selection, multiBet, oddsChangeEnum)
                    if (resp != null && resp.isSuccessful) {
                        tempDetail.forEach { detail ->
                            val info = resp.data.find { it.serialValue == detail.serialValue }
                            if (info != null) {
                                detail.orderId = info.orderId
                                detail.status = BetResultStatusEnum.getStatusByCode(info.orderStatus)
                                betDao.updateDetail(detail)
                            }
                        }
                    } else {
                        tempDetail.map { detail ->

                            betDao.updateDetailStatus(detail.betId, detail.serialValue, BetResultStatusEnum.FAIL)
                        }
                    }
                    betDao.getCurrentBet(BetStatusEnum.BETTING)?.let { bettingBet ->
                        betDao.updateBetStatus(bettingBet.betId, BetStatusEnum.COMPLETE)
                    }
                }
            }
        }
    }

    /**
     * 取消盘口订阅通知
     * @param selections
     */
    private fun unregister(selections: List<BetSelectionBean>) {
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
        //"aaaa---calculateMultiBetSums,data:$data,risk:$riskList".logd(TAG)
        for (k in n downTo 0) {
            riskMap[k]?.let { risk ->
                val odds = calculateCombinationOdds(oddsList,k)
                val count = when (k) {
                    0 -> 0
                    else -> CombinationExt.cNK(data.size,k)
                }
                totalSumOdds += odds
                totalCount += count

                if (k == 0) {//全串关
                    result.add(
                        ComboMultiBetBean(
                            serialValue = risk.serialValue,
                            comboK = n,
                            comboV = totalCount,
                            sumOdds = totalSumOdds,
                            odds = totalSumOdds / totalCount,
                            count = totalCount,
                            minAmount = risk.minAmount,
                            maxAmount = risk.maxAmount,
                        )
                    )
                } else {
                    result.add(
                        ComboMultiBetBean(
                            serialValue = risk.serialValue,
                            comboK = k,
                            comboV = 1,
                            sumOdds = odds,
                            odds = odds / count,
                            count = count,
                            minAmount = risk.minAmount,
                            maxAmount = risk.maxAmount,
                        )
                    )
                }

            }
        }

        //超级组合
        riskMap[ComboMultiBetBean.SERIAL_VALUE_SUPER]?.let { risk ->
            val odds = calculateCombinationOdds(oddsList,1)
            //"aaaa---hasSerialSuper,odds:$odds".logd(TAG)
            val count = data.size
            totalSumOdds += odds
            totalCount += count

            ComboMultiBetBean(
                serialValue = risk.serialValue,
                comboK = n,
                comboV = totalCount,
                sumOdds = totalSumOdds,
                odds = totalSumOdds / totalCount,
                count = totalCount,
                minAmount = risk.minAmount,
                maxAmount = risk.maxAmount,
            ).also {
                result.add(it)
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

        val riskMap = riskList.associateBy { it.serialValue }
        var totalSumOdds = 0

        for (k in n downTo 0) {
            riskMap[k]?.let { risk ->
                val odds = calculateCombinationOdds(oddsList,k)
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

        riskMap[ComboMultiBetBean.SERIAL_VALUE_SUPER]?.let { risk ->
            val odds = calculateCombinationOdds(oddsList,1)
            totalSumOdds += odds
            result[risk.serialValue] = ComboMultiBetOddsBean(
                serialValue = risk.serialValue,
                sumOdds = totalSumOdds
            )
        }
        return result
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
                    //"aaaa---updateOdds,selections:$selections".logd(TAG)
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

    fun setMoney(serialValue: Int, money: Long) {
        scope.launch {
            betDao.getCurrentBet()?.let { bet ->
                betDao.updateDetailMoney(bet.betId, serialValue, money)
            }
        }
    }

    /**
     * 拆分串关，比如3串4拆成2串1，3串1
     * @param combK
     * @param combV
     * @return
     */
    private fun splitComboIntoSingles(bean:ComboMultiBetBean,
                                      betList:List<BetSelectionBean>?,
                                      moneySymbol:String): List<ComboDetailFragment.ParameterItems> {
        val data = betList ?: return emptyList()
        // 1 注 = 固定只有一个 K
        val kList = if (bean.comboV == 1) {
            listOf(bean.comboK)
        } else {
            ((if(bean.isSuperCombo) 1 else 2)..bean.comboK).toList()
        }
        return kList.map { k ->
            val title = R.string.title_combo_bet_detail.getString(
                if(k==1) arch.cayenne.lib.res.R.string.title_single_bet.getString()
                else R.string.title_combo_bet_odds.getString(k,1)
            )
            val listItems = data.combination(k).map { l ->
                val ret = ComboDetailFragment.ParameterItems2(
                    combo = l.map{ data.indexOf(it)},
                    money = bean.inputMoney,
                    oddsList = l.map { it.odds },
                    moneySymbol = moneySymbol,
                )
                ret
            }
            ComboDetailFragment.ParameterItems(title, listItems)
        }
    }

    /**
     * 找出serialValue对应的组合详情Parameter
     *
     * @param serialValue
     * @param comboMultiBetBeans
     * @param betList
     * @param moneySymbol
     * @return
     */
    fun toCombinationDetailParameter(data:ComboMultiBetBean,
                                     betList:List<BetSelectionBean>?,
                                     moneySymbol:String): ComboDetailFragment.Parameter {
        val items = splitComboIntoSingles(data,betList,moneySymbol)
        val ret = ComboDetailFragment.Parameter(
            serialValue = data.serialValue,
            comboK = data.comboK,
            comboV = data.comboV,
            items = items
        )
        return ret
    }
}