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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class ComboBetRepository(
    override val scope: CoroutineScope,
    private val betDao: BetDao,
    private val remoteManager: BettingRemoteManager
) : BaseRepository() {

    private val selectionFlow = MutableSharedFlow<List<BetSelectionBean>>(replay = 1, extraBufferCapacity = 1)
    private val comboMultiBetFlow = MutableSharedFlow<List<ComboMultiBetBean>>(replay = 1, extraBufferCapacity = 1)

    init {
        scope.launch {
            betDao.getCurrentBet()?.let {  bet ->
                betDao.observeSelections(bet.betId).collect {
                    selectionFlow.emit(it)
                    if (it.isNotEmpty()) {
                        val detail = betDao.getDetail(bet.betId)
                        setComboMulti(it, detail)
                    }
                }
            }
        }
    }

    private suspend fun setComboMulti(data: List<BetSelectionBean>, detailList: List<BetDetailBean>? = null) {
        remoteManager.getComboRisk(data)?.let { riskList ->
            val multiBet = calculateMultiBetSums(data, riskList).map { bean ->
                val detail = detailList?.find { it.combo == bean.combo }
                if (detail == null) {
                    bean
                } else {
                    ComboMultiBetBean(
                        combo = bean.combo,
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
            val emptyData = listOf(ComboRiskDataModel(1, 0, 0))
            val multiBet = calculateMultiBetSums(data, emptyData)
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
                    remoteManager.unregisterMatchNotify(listOf(selection.matchId))
                }
            }
        }
    }

    fun removeAll() {
        scope.launch {
            betDao.removeCurrentBet()
        }
    }

    fun saveToSingleBet() {
        scope.launch {
            betDao.getCurrentBet()?.let {
                betDao.updateBetType(it.betId, BetTypeEnum.SINGLE)
            }
        }
    }

    fun saveInputMoney(data: List<ComboMultiBetBean>) {
        scope.launch {
            betDao.getCurrentBet()?.let { bet ->
                if (bet.betType == BetTypeEnum.COMBO) {
                    val betId = bet.betId
                    val detailBean = data.map { bean ->
                        BetDetailBean(
                            betId = betId,
                            combo = bean.combo,
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
            betDao.getCurrentBet()?.let {  bet ->
                if (bet.betType == BetTypeEnum.COMBO) {
                    val betId = bet.betId
                    betDao.updateBetStatus(betId, BetStatusEnum.BETTING)

                    val selection = betDao.getSelections(betId)
                    val tempDetail = multiBet.map { bean ->
                        BetDetailBean(
                            betId = betId,
                            combo = bean.combo,
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
                            val res = resp.data.first { it.comboValue == bean.combo }
                            BetDetailBean(
                                betId = betId,
                                combo = res.comboValue,
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
                                betId = betId,
                                combo = bean.combo,
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

        val riskMap = riskList.associateBy { it.combo }

        for (k in n downTo 1) {
            riskMap[k]?.let { risk ->
                val combinations = data.combinations(k)
                val odds = when (k) {
                    1 -> oddsList.reduce { acc, l -> acc.getOdds(l).toOdds() }
                    n -> oddsList.sum()
                    else -> oddsList.combinations(k)
                        .sumOf { it.reduce { acc, l -> acc.getOdds(l).toOdds() } }
                }
                val count = when (k) {
                    1 -> 1
                    n -> n
                    else -> combinations.size
                }

                result.add(
                    ComboMultiBetBean(
                        combo = risk.combo,
                        sumOdds = odds,
                        count = count,
                        minAmount = risk.minAmount,
                        maxAmount = risk.maxAmount
                    )
                )
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
}