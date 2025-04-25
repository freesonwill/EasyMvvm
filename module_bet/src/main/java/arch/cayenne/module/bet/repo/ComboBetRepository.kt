package arch.cayenne.module.bet.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.common.utils.ext.SportIntExt.getOdds
import arch.cayenne.lib.common.utils.ext.SportStringExt.toOdds
import arch.cayenne.lib.database.dao.BetDao
import arch.cayenne.lib.database.dao.BetResultDao
import arch.cayenne.lib.database.entity.BetBean
import arch.cayenne.lib.database.entity.BetResultBean
import arch.cayenne.lib.database.entity.BetResultDetailBean
import arch.cayenne.lib.database.entity.BetResultStatusEnum
import arch.cayenne.lib.database.entity.BetStatusEnum
import arch.cayenne.lib.database.entity.BetTypeEnum
import arch.cayenne.module.bet.BettingRemoteManager
import arch.cayenne.module.bet.data.ComboMultiBetBean
import arch.cayenne.module.bet.data.remote.ComboRiskDataModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ComboBetRepository(
    override val scope: CoroutineScope,
    private val betDao: BetDao,
    private val betResultDao: BetResultDao,
    private val remoteManager: BettingRemoteManager
) : BaseRepository() {

    private val comboMultiBetFlow = MutableSharedFlow<List<ComboMultiBetBean>>()

    init {
        scope.launch {
            betDao.observeComboBet().collect { bets ->
                if (bets.isNotEmpty()) {
                    remoteManager.getComboRisk(bets)?.let { riskList ->
                        val multiBet = calculateMultiBetSums(bets, riskList)
                        comboMultiBetFlow.emit(multiBet)
                    }
                }
            }
        }
    }

    fun observeComboBet() = betDao.observeComboBet()
    fun observeComboMultiBet(): Flow<List<ComboMultiBetBean>> = comboMultiBetFlow

    fun removeBet(id: Long) {
        scope.launch {
            betDao.removeBet(id)
        }
    }

    fun removeAll() {
        scope.launch {
            betDao.deleteAll()
        }
    }

    fun saveToSingleBet(id: Long) {
        scope.launch {
            betDao.updateBetType(id, BetTypeEnum.SINGLE)
        }
    }

    suspend fun sendBet(multiBet: List<ComboMultiBetBean>) = withContext(scope.coroutineContext) {
        var resultId: Long? = null
        val betBeans = betDao.getComboBet()
        val ids = betBeans.map { it.matchId }
        betDao.updateBetListStatus(ids, BetStatusEnum.BETTING)
        val resp = remoteManager.comboBet(betBeans, multiBet)
        if (resp != null && resp.isSuccessful) {
            val resultBean = multiBet.map { bean ->
                val result = resp.data.first { it.comboValue == bean.combo }
                BetResultDetailBean(
                    orderId = result.orderId,
                    combo = bean.combo,
                    sumOdds = bean.sumOdds,
                    count = bean.count,
                    inputMoney = bean.inputMoney,
                    statusEnum = BetResultStatusEnum.getStatusByCode(result.orderStatus)
                )
            }
            val result = BetResultBean(
                selectionIds = betBeans.map { it.selectionLiteBean.id },
                detail = resultBean
            )
            betResultDao.insert(result)
            resultId = result.id
        }
        betDao.updateBetListStatus(ids, BetStatusEnum.COMPLETE)
        resultId
    }

    private fun calculateMultiBetSums(
        data: List<BetBean>,
        riskList: List<ComboRiskDataModel>
    ): List<ComboMultiBetBean> {
        val result = mutableListOf<ComboMultiBetBean>()
        val oddsList = data.map { it.selectionLiteBean.odds.toOdds() }
        val n = data.size

        val riskMap = riskList.associateBy { it.count }

        for (k in n downTo 1) {
            riskMap[k]?.let { risk ->
                val combinations = data.combinations(k)
                val odds = when (k) {
                    1 -> oddsList.reduce { acc, l -> acc.getOdds(l).toOdds() }
                    n -> oddsList.sum()
                    else -> oddsList.combinations(k)
                        .sumOf { it.reduce { acc, l -> acc.getOdds(l).toOdds() } }
                }
                val count = combinations.size

                result.add(
                    ComboMultiBetBean(
                        combo = risk.count,
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